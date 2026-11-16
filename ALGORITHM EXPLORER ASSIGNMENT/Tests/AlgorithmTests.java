import core.*;
import sorting.*;
import recursion.*;
import structures.*;
import analysis.*;
import java.util.*;

public class AlgorithmTests {

  static int fails = 0;       // Stores the total number of failed tests.

  // Checks whether a condition is true and records a failure if it is false.
  static void check(boolean c, String m) { 
    if (!c) { 
      fails++; 
      System.out.println("FAIL: " + m); 
    } 
  }      

  static void expectThrow(Runnable r, String m) { 
    try { r.run(); check(false, "no exception: " + m); 

    } catch (RuntimeException e) { } 
  }      


  public static void main(String[] x) {

    // Creates a random number generator with a fixed seed.
    Random r = new Random(5);

    // Tests all sorting algorithms registered in the SortRegistry.
    for (String name : SortRegistry.NAMES) {

      // Runs 200 test cases for each sorting algorithm.
      for (int t = 0; t < 200; t++) {

        // Generates an array containing random integers.
        int n = r.nextInt(30); 
        int[] a = new int[n]; 
       
        for (int i = 0; i < n; i++) 
          a[i] = r.nextInt(12);

        // Creates a reference sorted array using Java's built-in sorting method.
        int[] e = a.clone(); 
        Arrays.sort(e);

        // Creates a copy of the original array and initializes sorting tracer and metrics.
        int[] b = a.clone(); 
        SortTracer tr = new SortTracer(true); 
        Metrics m = new Metrics();

        // Executes the selected sorting algorithm with tracing enabled.
        SortRegistry.sort(name, b, m, tr);

        // Checks whether the traced sorting result matches the reference result.
        check(Arrays.equals(b, e), name + " traced sort " + Arrays.toString(a));

        // Checks that the sorting tracer recorded at least one frame.
        check(tr.size() >= 1, name + " frames");

        // Gets the final sorting frame and checks whether it contains the sorted array.
        SortTracer.Frame last = tr.get(tr.size()-1); 
        check(Arrays.equals(last.values, e), name + " last frame");

        // Checks that every tracing frame has the correct array and marker lengths.
        for (int f = 0; f < tr.size(); f++) { 
          check(tr.get(f).marks.length == n && tr.get(f).values.length == n, name + " frame size"); }

        // Executes the same sorting algorithm without tracing and checks the result.
        int[] c = a.clone(); 
        SortRegistry.sort(name, c, new Metrics(), SortTracer.OFF); 
        check(Arrays.equals(c, e), name + " untraced");
      }
    }

    // Displays comparison and movement counts for a known input array.
    // counts for known input
    int[] in = {45,12,89,23,7,64,31};

    // Runs every registered sorting algorithm on the known input.
    for (String name : SortRegistry.NAMES) { Metrics m = new Metrics(); 
      SortRegistry.sort(name, in.clone(), m, SortTracer.OFF); 
      System.out.println(name + ": comparisons=" + m.getComparisons() + " moves=" + m.getMoves()); }

    // Tests the best case performance of insertion sort on an already sorted array.
    // sorted input: insertion/bubble best case n-1 compares
    Metrics m = new Metrics(); 
    SortRegistry.sort(SortRegistry.INSERTION, InputGenerator.sorted(1000), m, SortTracer.OFF); 
    check(m.getComparisons() == 999 && m.getMoves() == 0, "insertion best");

    // Tests the best case performance of bubble sort on an already sorted array.
    m = new Metrics(); SortRegistry.sort(SortRegistry.BUBBLE, InputGenerator.sorted(1000), m, SortTracer.OFF); check(m.getComparisons() == 999, "bubble best");

    // Tests quicksort's worst case recursion using a larger stack.
    final int[] big = InputGenerator.sorted(100000);

    // Executes quicksort on sorted data and checks its worst case comparison count.
    BigStack.run(() -> { Metrics mm = new Metrics(); 
    long t0 = System.nanoTime(); SortRegistry.sort(SortRegistry.QUICK_FIRST, InputGenerator.sorted(20000), mm, SortTracer.OFF); 
    System.out.println("quick first sorted 20000: cmp=" + mm.getComparisons() + " " + (System.nanoTime()-t0)/1e6 + "ms");
    check(mm.getComparisons() == 20000L*19999/2, "quick worst count"); });

    // Tests the recursive Fibonacci implementation and its number of recursive calls.
    m = new Metrics(); check(FibonacciAlgorithms.naive(30, m) == 832040 && m.getCalls() == 2692537, "fib naive 30 " + m.getCalls());

    // Tests the memoized Fibonacci implementation.
    m = new Metrics(); check(FibonacciAlgorithms.memoized(30, m) == 832040, "memo"); System.out.println("memo calls 30 = " + m.getCalls());

    // Tests the bottom-up dynamic programming Fibonacci implementation.
    m = new Metrics(); check(FibonacciAlgorithms.bottomUp(30, m) == 832040 && m.getCalls() == 29, "dp");

    // Compares memoized and bottom-up Fibonacci results for input 90.
    check(FibonacciAlgorithms.memoized(90, new Metrics()) == 2880067194370816120L && FibonacciAlgorithms.bottomUp(90, new Metrics()) == 2880067194370816120L, "fib90");

    // Tests Fibonacci calculations for small input values.
    check(FibonacciAlgorithms.bottomUp(0, new Metrics()) == 0 && FibonacciAlgorithms.naive(0, new Metrics()) == 0 && FibonacciAlgorithms.memoized(1, new Metrics()) == 1, "fib small");

    // Checks that recursive and iterative factorial methods return 1 for zero.
    check(FactorialAlgorithms.recursive(0, new Metrics()) == 1 && FactorialAlgorithms.iterative(0, new Metrics()) == 1, "0!");

    // Compares recursive and iterative factorial results for the value 20.
    check(FactorialAlgorithms.recursive(20, new Metrics()) == FactorialAlgorithms.iterative(20, new Metrics()) && FactorialAlgorithms.iterative(20, new Metrics()) == 2432902008176640000L, "20!");

    // Checks that negative factorial input and values exceeding the supported range throw exceptions.
    expectThrow(() -> FactorialAlgorithms.recursive(-1, new Metrics()), "neg"); expectThrow(() -> FactorialAlgorithms.iterative(21, new Metrics()), "21");

    // Tests the Tower of Hanoi algorithm with different numbers of disks.
    for (int n = 1; n <= 10; n++) { m = new Metrics(); HanoiMove[] mv = TowerOfHanoi.solve(n, 'B', 'A', 'C', m);

      // Checks that the number of moves and recursive calls match the expected value.
      check(mv.length == (1<<n)-1 && m.getCalls() == mv.length, "hanoi count " + n);

      // Simulates the three pegs using custom stacks.
      CustomStack[] pegs = new CustomStack[3]; for (int i = 0; i < 3; i++) pegs[i] = new CustomStack(n);

      // Places all disks on the starting peg in descending order.
      for (int d = n; d >= 1; d--) pegs['B'-'A'].push(d);

      // Executes each move and checks that the correct disk is moved.
      // Also checks that a larger disk is never placed on a smaller disk.
      for (HanoiMove h : mv) { 
        int d = pegs[h.from-'A'].pop(); 
      check(d == h.disk, "disk"); 
      if (!pegs[h.to-'A'].isEmpty()) check(pegs[h.to-'A'].peek() > d, "illegal move"); 
      pegs[h.to-'A'].push(d); 
    }

      // Checks that all disks finish on the destination peg.
      check(pegs[0].size() == n && pegs[1].isEmpty() && pegs[2].isEmpty(), "hanoi end " + n);
    }

    // Checks that using the same starting and destination peg is rejected.
    expectThrow(() -> TowerOfHanoi.solve(3, 'A', 'A', 'B', new Metrics()), "same pegs");

    // Tests stack operations, including insertion, removal, overflow, and underflow.
    CustomStack s = new CustomStack(2); 
    s.push(1); 
    s.push(2); 
    expectThrow(() -> s.push(3), "overflow"); 
    check(s.peek() == 2 && s.pop() == 2 && s.pop() == 1, "stack"); 
    expectThrow(() -> s.pop(), "underflow");

    // Tests the behavior of linear and circular queues against expected results.
    LinearQueue lq = new LinearQueue(5);

    // Fills the linear queue and removes two elements.
    for (int i = 1; i <= 5; i++) lq.enqueue(i); lq.dequeue(); lq.dequeue();

    // Checks that the linear queue correctly reports its false-full condition.
    try { lq.enqueue(9); 
      check(false, "false full"); 
    } 
    catch (IllegalStateException e) { 
      check(e.getMessage().contains("FALSE FULL"), "msg"); 
    }

    // Tests circular queue insertion, removal, and wraparound behavior.
    CircularQueue cq = new CircularQueue(5); 
    for (int i = 1; i <= 5; i++) cq.enqueue(i); 
    cq.dequeue(); cq.dequeue(); cq.enqueue(6); 
    cq.enqueue(7);

    // Checks whether the circular queue is full and verifies its front and rear indexes.
    check(cq.isFull() && cq.rearIndex() == 1 && cq.frontIndex() == 2 && cq.display().equals("[3, 4, 5, 6, 7]"), "circular " + cq.display() + " r=" + cq.rearIndex());

    // Creates a reference queue using Java's ArrayDeque for comparison.
    Random q = new Random(9); ArrayDeque<Integer> ref = new ArrayDeque<>(); 
    CircularQueue c2 = new CircularQueue(7);

    // Performs 2000 random enqueue and dequeue operations.
    for (int i = 0; i < 2000; i++) {

      // Randomly decides whether to insert or remove an element.
      if (q.nextBoolean()) {

        // Inserts an element if there is available space
        if (ref.size() < 7) { 
          c2.enqueue(i); 
          ref.add(i); 
        } 
        else expectThrow(() -> c2.enqueue(1), "full");

      } else {

        // Removes an element if the reference queue is not empty
        if (!ref.isEmpty()) check(c2.dequeue() == ref.poll(), "cq deq"); 
        else expectThrow(() -> c2.dequeue(), "empty");
      }

      // Checks that the custom queue and reference queue have the same size.
      check(c2.size() == ref.size(), "cq size");

      // Counts the occupied positions in the circular queue.
      int occ = 0; for (int k = 0; k < 7; k++) 
        if (c2.isOccupied(k)) occ++; 
      check(occ == ref.size(), "occupied"); 
    }

    // Tests singly and doubly linked lists.
    Random lr = new Random(3); 
    SinglyLinkedList sl = new SinglyLinkedList(); 
    DoublyLinkedList dl = new DoublyLinkedList(); 
    LinkedList<Integer> ref2 = new LinkedList<>();

    // Performs 6000 randomly selected linked list operations.
    for (int step = 0; step < 6000; step++) {

      // Generates an operation, a value, and a target value for each step.
      // Checks whether the target exists and finds its position.
      int op = lr.nextInt(10), v = lr.nextInt(8), tg = lr.nextInt(8); 
      boolean has = ref2.contains(tg); 
      int idx = ref2.indexOf(tg);

      try {

        // Selects a linked list operation based on the randomly generated operation number.
        switch (op) {

          //Inserts a value at the beginning of both linked lists.
          case 0: 
          sl.insertAtBeginning(v); 
          dl.insertAtBeginning(v); 
          ref2.addFirst(v); 
          break;

          //Inserts a value at the end of both linked lists.
          case 1: 
          sl.insertAtEnd(v); 
          dl.insertAtEnd(v); 
          ref2.addLast(v); 
          break;

          //Inserts a value before the target if it exists otherwise, checks that an exception is thrown.
          case 2: 
          if (!has) { 
            expectThrow(() -> sl.insertBefore(tg, v), "ib"); 
            expectThrow(() -> dl.insertBefore(tg, v), "ib"); 
          } 
          else { 
            sl.insertBefore(tg, v); 
            dl.insertBefore(tg, v); 
            ref2.add(idx, v);
          } break;

          //Inserts a value after the target if it exists.
          case 3:
           if (!has) {
             expectThrow(() -> sl.insertAfter(tg, v), "ia"); 
             expectThrow(() -> dl.insertAfter(tg, v), "ia"); 
            }
             else { 
              sl.insertAfter(tg, v); 
              dl.insertAfter(tg, v); 
              ref2.add(idx+1, v);
            } break;

          //Deletes the first element from both linked lists.
          case 4: 
          if (ref2.isEmpty()) { 
            expectThrow(() -> sl.deleteAtBeginning(), "db"); 
            expectThrow(() -> dl.deleteAtBeginning(), "db");
           } 
           else { 
            int e = ref2.removeFirst(); 
            check(sl.deleteAtBeginning() == e && dl.deleteAtBeginning() == e, "db val"); 
          } break;

          //Deletes the last element from both linked lists.
          case 5: 
          if (ref2.isEmpty()) { 
            expectThrow(() -> sl.deleteAtEnd(), "de"); 
            expectThrow(() -> dl.deleteAtEnd(), "de"); 
          } 
          else { 
            int e = ref2.removeLast(); 
            check(sl.deleteAtEnd() == e && dl.deleteAtEnd() == e, "de val");
           } break;

          //Deletes the element immediately after the target.
          case 6: 
          if (!has || idx == ref2.size()-1) { expectThrow(() -> sl.deleteAfter(tg), "da"); 
           expectThrow(() -> dl.deleteAfter(tg), "da"); 
         } 
         else { 
           int e = ref2.remove(idx+1); 
           check(sl.deleteAfter(tg) == e && dl.deleteAfter(tg) == e, "da val"); 
          } break;

          // Deletes the element immediately before the target.
          case 7: 
          if (!has || idx == 0) { expectThrow(() -> sl.deleteBefore(tg), "dbf"); 
           expectThrow(() -> dl.deleteBefore(tg), "dbf"); 
          } else { 
             int e = ref2.remove(idx-1); 
             check(sl.deleteBefore(tg) == e && dl.deleteBefore(tg) == e, "dbf val"); 
          } break;

          //Deletes the target value from both linked lists.
          case 8: 
          if (!has) { 
           expectThrow(() -> sl.delete(tg), "d"); expectThrow(() -> dl.delete(tg), "d"); } else { ref2.remove(idx); sl.delete(tg); dl.delete(tg); } break;

          // Default case: Searches for the target and compares its position in both lists.
          default: 
          check(sl.search(tg) == idx && dl.search(tg) == idx, "search"); 
          break;
        }

      // Catches unexpected runtime exceptions during linked list testing.
      } catch (RuntimeException e) { 
        check(false, "unexpected " + e + " op " + op); 
      }

      // Converts the reference linked list into an integer array.
      int[] ra = new int[ref2.size()];
       int k = 0; 
       for (int e : ref2) ra[k++] = e;

      // Checks whether both custom linked lists match the reference list and also verifies that their sizes are correct.
      check(Arrays.equals(sl.toArray(), ra) && Arrays.equals(dl.toArray(), ra) && sl.size() == ra.length && dl.size() == ra.length, "list state step " + step + " op " + op);

      // Every 500 steps, checks whether backward display is the reverse of forward display.
      if (step % 500 == 0) { 
        StringBuilder fwd = new StringBuilder(); 
        StringBuilder back = new StringBuilder();

        // backward must be reverse of forward
        String b = dl.displayBackward(); String f = dl.displayForward(); String[] fp = f.split(" <-> "); String[] bp = b.split(" <-> "); boolean ok = fp.length == bp.length; for (int i = 0; ok && i < fp.length; i++) ok = fp[i].equals(bp[bp.length-1-i]); check(ok, "backward " + f + " vs " + b); }
    }

    // Tests the bracket checker using balanced and unbalanced expressions.
    StringBuilder log = new StringBuilder();

    // Checks balanced brackets, mismatched brackets, unclosed brackets, extra closing brackets, and an empty expression.
    check(BracketChecker.check("{ ( [ ] ) }", log) , "balanced"); check(!BracketChecker.check("{ ( ] }", new StringBuilder()), "mismatch"); check(!BracketChecker.check("((", new StringBuilder()), "unclosed"); check(!BracketChecker.check(")", new StringBuilder()), "extra close"); check(BracketChecker.check("", new StringBuilder()), "empty");

    // Displays the final test result based on the number of failed tests.
    System.out.println(fails == 0 ? "ALL TESTS PASSED" : fails + " FAILURES");

    System.exit(fails == 0 ? 0 : 1);
  }
}

