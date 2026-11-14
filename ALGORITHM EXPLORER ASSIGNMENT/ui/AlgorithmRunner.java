package ui;

import analysis.ComplexityAnalyzer;
import core.AlgorithmNames;
import core.ArrayUtil;
import core.BufferedLogger;
import core.OperationCounter;
import core.StepLogger;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import recursion.Factorial;
import recursion.Fibonacci;
import recursion.TowerOfHanoi;
import searching.BinarySearch;
import searching.LinearSearch;
import sorting.BubbleSort;
import sorting.MergeSort;
import sorting.QuickSort;
import structures.CustomBST;
import structures.CustomLinkedList;
import structures.CustomQueue;
import structures.CustomStack;

/**
 * Connect to the window to the algorithms and everything runs on a background thread (SwingWorker),
 * so the window never freezes. Each run is executed twice:
 * no logging to measure the operation count and the running time,
 * with logging, to produce the step-by-step output.
 */
public class AlgorithmRunner {

    private static final int MAX_ARRAY = 20000;
    private static final int MAX_STRUCTURE = 2000;
    private static final int MAX_LOG_LINES = 3000;
    private static final long SLOW_RUN_NANOS = 50_000_000L; // stop repeating timing runs once one takes 50 ms

    /** One runnable unit of work. Returns a short summary that is shown at the end of the log. */
    private interface Task {
        String run(OperationCounter ops, StepLogger log);
    }

    private static final class Result {
        final String log, ops, time, complexity;

        Result(String log, String ops, String time, String complexity) {
            this.log = log;
            this.ops = ops;
            this.time = time;
            this.complexity = complexity;
        }
    }

    private final ExplorerFrame frame;

    public AlgorithmRunner(ExplorerFrame frame) {
        this.frame = frame;
        frame.onRun(this::start);
    }

    private void start() {
        final String category = frame.getCategory();
        final String algorithm = frame.getAlgorithm();
        final String input = frame.getInputText();
        final String target = frame.getTargetText();
        if (algorithm == null) return;

        frame.setBusy(true);
        new SwingWorker<Result, Void>() {
            @Override
            protected Result doInBackground() {
                return runSelection(category, algorithm, input, target);
            }

            @Override
            protected void done() {
                try {
                    Result r = get();
                    frame.showResult(r.log, r.ops, r.time, r.complexity);
                } catch (ExecutionException ex) {
                    frame.showError(describeError(ex.getCause()));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    frame.showError("The run was interrupted.");
                } finally {
                    frame.setBusy(false);
                }
            }
        }.execute();
    }

    private static String describeError(Throwable t) {
        if (t instanceof StackOverflowError) {
            return "Stack overflow: the recursion went too deep for this input. Try fewer values.";
        }
        if (t instanceof OutOfMemoryError) {
            return "Out of memory: try a smaller input.";
        }
        return t.getMessage() != null ? t.getMessage() : t.toString();
    }



    private Result runSelection(String category, String algorithm, String input, String target) {
        if (AlgorithmNames.BENCHMARK.equals(algorithm)) {
            String report = ComplexityAnalyzer.runBenchmark();
            return new Result(report, "see report", "see report", ComplexityAnalyzer.describe(algorithm));
        }

        Task task = buildTask(category, algorithm, input, target);

        // Pass 1: silent runs for timing. The best of up to 5 runs is reported, which reduces JIT warm-up noise.
        OperationCounter timedOps = new OperationCounter();
        long best = Long.MAX_VALUE;
        for (int run = 0; run < 5; run++) {
            timedOps.reset();
            long start = System.nanoTime();
            task.run(timedOps, StepLogger.NONE);
            long elapsed = System.nanoTime() - start;
            best = Math.min(best, elapsed);
            if (elapsed > SLOW_RUN_NANOS) break;
        }

        // Pass 2: logged run for the step-by-step output.
        BufferedLogger logger = new BufferedLogger(MAX_LOG_LINES);
        String summary = task.run(new OperationCounter(), logger);

        String text = logger.getText() + "\n" + summary + "\n";
        String time = String.format("%.4f ms", best / 1e6);
        return new Result(text, String.valueOf(timedOps.get()), time, ComplexityAnalyzer.describe(algorithm));
    }

    private Task buildTask(String category, String algorithm, String input, String target) {
        switch (category) {
            case AlgorithmNames.SORTING:
                return sortingTask(algorithm, ArrayUtil.parse(input, MAX_ARRAY));
            case AlgorithmNames.SEARCHING:
                return searchingTask(algorithm, ArrayUtil.parse(input, MAX_ARRAY),
                        ArrayUtil.parseInt(target, "Target value"));
            case AlgorithmNames.RECURSION:
                return recursionTask(algorithm, ArrayUtil.parseInt(input, "Input"));
            case AlgorithmNames.DATA_STRUCTURES:
                return structureTask(algorithm, ArrayUtil.parse(input, MAX_STRUCTURE), target);
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }

    //sorting 

    private Task sortingTask(String algorithm, int[] original) {
        return (ops, log) -> {
            int[] a = original.clone();
            if (log.enabled()) log.log("Initial array: " + ArrayUtil.format(a) + "\n");
            switch (algorithm) {
                case AlgorithmNames.BUBBLE_SORT: BubbleSort.sort(a, ops, log); break;
                case AlgorithmNames.QUICK_SORT:  QuickSort.sort(a, ops, log); break;
                case AlgorithmNames.MERGE_SORT:  MergeSort.sort(a, ops, log); break;
                default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }
            return log.enabled() ? "Final sorted array: " + ArrayUtil.format(a) : "";
        };
    }

    //searching

    private Task searchingTask(String algorithm, int[] original, int target) {
        return (ops, log) -> {
            int[] a = original.clone();
            if (log.enabled()) {
                log.log("Array input: " + ArrayUtil.format(a));
                log.log("Target value: " + target + "\n");
            }
            int index;
            if (AlgorithmNames.BINARY_SEARCH.equals(algorithm)) {
                // Binary search needs ordered data, so the array is sorted first (silently, and not counted).
                QuickSort.sort(a, new OperationCounter(), StepLogger.NONE);
                if (log.enabled()) {
                    log.log("Binary search needs sorted data, so the array was sorted first (not counted):");
                    log.log("Sorted array: " + ArrayUtil.format(a) + "\n");
                }
                index = BinarySearch.search(a, target, ops, log);
            } else {
                index = LinearSearch.search(a, target, ops, log);
            }
            if (!log.enabled()) return "";
            return index >= 0 ? "SUCCESS: found " + target + " at index " + index + "."
                              : "RESULT: " + target + " was not found in the array.";
        };
    }

    //recursion

    private Task recursionTask(String algorithm, int n) {
        return (ops, log) -> {
            switch (algorithm) {
                case AlgorithmNames.FACTORIAL: {
                    long result = Factorial.compute(n, ops, log);
                    return "Final result: " + n + "! = " + result;
                }
                case AlgorithmNames.FIB_NAIVE: {
                    long result = Fibonacci.naive(n, ops, log);
                    return "Final result: fib(" + n + ") = " + result
                            + "  (note how many calls were needed: see the operation count)";
                }
                case AlgorithmNames.FIB_MEMO: {
                    long result = Fibonacci.memoized(n, ops, log);
                    return "Final result: fib(" + n + ") = " + result;
                }
                case AlgorithmNames.HANOI: {
                    long moves = TowerOfHanoi.solve(n, ops, log);
                    return "Solved " + n + " disks in " + moves + " moves (2^" + n + " - 1).";
                }
                default:
                    throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }
        };
    }

    //data structures

    private Task structureTask(String algorithm, int[] data, String targetText) {
        final boolean hasTarget = !targetText.isEmpty();
        final int target = hasTarget ? ArrayUtil.parseInt(targetText, "Target value") : 0;

        return (ops, log) -> {
            switch (algorithm) {
                case AlgorithmNames.STACK:       return stackDemo(data, hasTarget, target, ops, log);
                case AlgorithmNames.QUEUE:       return queueDemo(data, hasTarget, target, ops, log);
                case AlgorithmNames.LINKED_LIST: return listDemo(data, hasTarget, target, ops, log);
                case AlgorithmNames.BST:         return bstDemo(data, hasTarget, target, ops, log);
                default: throw new IllegalArgumentException("Unknown structure: " + algorithm);
            }
        };
    }

    private String stackDemo(int[] data, boolean hasExtra, int extra, OperationCounter ops, StepLogger log) {
        CustomStack stack = new CustomStack(4, ops);
        if (log.enabled()) log.log("--- PUSH (bottom -> top) ---");
        for (int v : data) {
            stack.push(v);
            if (log.enabled()) log.log("Pushed " + v + " | stack: " + stack);
        }
        if (hasExtra) {
            stack.push(extra);
            if (log.enabled()) log.log("Pushed extra value " + extra + " | stack: " + stack);
        }
        if (log.enabled()) log.log("\n--- POP (the last value pushed leaves first) ---");
        int popped = stack.pop();
        if (log.enabled()) log.log("Popped " + popped + " | stack: " + stack);
        return log.enabled() ? "Stack now holds " + stack.size() + " value(s); top is "
                + (stack.isEmpty() ? "empty" : String.valueOf(stack.peek())) + "." : "";
    }

    private String queueDemo(int[] data, boolean hasExtra, int extra, OperationCounter ops, StepLogger log) {
        CustomQueue queue = new CustomQueue(4, ops);
        if (log.enabled()) log.log("--- ENQUEUE (front -> rear) ---");
        for (int v : data) {
            queue.enqueue(v);
            if (log.enabled()) log.log("Enqueued " + v + " | queue: " + queue);
        }
        if (hasExtra) {
            queue.enqueue(extra);
            if (log.enabled()) log.log("Enqueued extra value " + extra + " | queue: " + queue);
        }
        if (log.enabled()) log.log("\n--- DEQUEUE (the first value enqueued leaves first) ---");
        int removed = queue.dequeue();
        if (log.enabled()) log.log("Dequeued " + removed + " | queue: " + queue);
        return log.enabled() ? "Queue now holds " + queue.size() + " value(s); front is "
                + (queue.isEmpty() ? "empty" : String.valueOf(queue.peek())) + "." : "";
    }

    private String listDemo(int[] data, boolean hasTarget, int target, OperationCounter ops, StepLogger log) {
        CustomLinkedList list = new CustomLinkedList(ops);
        if (log.enabled()) log.log("--- INSERT AT END ---");
        for (int v : data) {
            list.insertAtEnd(v);
            if (log.enabled()) log.log("Inserted " + v + " | list: " + list);
        }
        if (hasTarget) {
            if (log.enabled()) log.log("\n--- SEARCH ---");
            int index = list.search(target);
            if (log.enabled()) {
                log.log(index >= 0 ? target + " found at position " + index : target + " is not in the list");
                log.log("\n--- DELETE ---");
            }
            boolean removed = list.delete(target);
            if (log.enabled()) {
                log.log(removed ? "Deleted first node holding " + target + " | list: " + list
                                : "Nothing deleted: " + target + " is not in the list");
            }
        }
        return log.enabled() ? "List holds " + list.size() + " node(s)." : "";
    }

    private String bstDemo(int[] data, boolean hasTarget, int target, OperationCounter ops, StepLogger log) {
        CustomBST tree = new CustomBST(ops);
        if (log.enabled()) log.log("--- INSERTIONS ---");
        for (int v : data) tree.insert(v, log);
        if (log.enabled()) {
            log.log("\nTree has " + tree.size() + " node(s) and " + tree.height() + " level(s).");
            log.log("In-order   (sorted):         " + tree.inOrder());
            log.log("Pre-order  (root first):     " + tree.preOrder());
            log.log("Post-order (root last):      " + tree.postOrder());
        } else {
            tree.inOrder();
            tree.preOrder();
            tree.postOrder();
        }
        if (hasTarget) {
            if (log.enabled()) log.log("\n--- SEARCH " + target + " ---");
            boolean found = tree.search(target, log);
            if (log.enabled()) log.log("\n--- DELETE " + target + " ---");
            boolean removed = tree.delete(target);
            if (log.enabled()) {
                log.log(removed ? "Deleted " + target + ". In-order now: " + tree.inOrder()
                                : "Nothing deleted: " + target + " is not in the tree.");
            }
            return log.enabled() ? "Search " + (found ? "found" : "did not find") + " " + target + "." : "";
        }
        return "";
    }
}
