package recursion;

import core.OperationCounter;
import core.StepLogger;

/**
 * Two recursive Fibonacci versions, to show the effect of repeated work.
 * Operation = one recursive call in both versions.
 */
public final class Fibonacci {
    public static final int MAX_NAIVE = 35;
    public static final int MAX_MEMO = 90; // fib(92) is the largest value that fits in a long

    private Fibonacci() { }

    /** Plain recursion: fib(n) = fib(n-1) + fib(n-2). Exponential number of calls. */
    public static long naive(int n, OperationCounter ops, StepLogger log) {
        check(n, MAX_NAIVE);
        return naive(n, 1, ops, log);
    }

    private static long naive(int n, int depth, OperationCounter ops, StepLogger log) {
        ops.increment();
        if (log.enabled()) log.log(indent(depth) + "fib(" + n + ")");
        if (n <= 1) return n;
        return naive(n - 1, depth + 1, ops, log) + naive(n - 2, depth + 1, ops, log);
    }

    /** Recursion with a lookup table: each value is computed once. Linear number of calls. */
    public static long memoized(int n, OperationCounter ops, StepLogger log) {
        check(n, MAX_MEMO);
        long[] memo = new long[n + 1];
        for (int i = 0; i <= n; i++) memo[i] = -1;
        return memoized(n, memo, 1, ops, log);
    }

    private static long memoized(int n, long[] memo, int depth, OperationCounter ops, StepLogger log) {
        ops.increment();
        if (n <= 1) {
            if (log.enabled()) log.log(indent(depth) + "fib(" + n + ") = " + n + " (base case)");
            return n;
        }
        if (memo[n] != -1) {
            if (log.enabled()) log.log(indent(depth) + "fib(" + n + ") = " + memo[n] + " (found in table)");
            return memo[n];
        }
        if (log.enabled()) log.log(indent(depth) + "fib(" + n + ") not in table, computing");
        memo[n] = memoized(n - 1, memo, depth + 1, ops, log) + memoized(n - 2, memo, depth + 1, ops, log);
        if (log.enabled()) log.log(indent(depth) + "fib(" + n + ") = " + memo[n] + " (stored)");
        return memo[n];
    }

    private static void check(int n, int max) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative.");
        if (n > max) throw new IllegalArgumentException("n must be at most " + max + " for this version.");
    }

    private static String indent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) sb.append("  ");
        return sb.toString();
    }
}
