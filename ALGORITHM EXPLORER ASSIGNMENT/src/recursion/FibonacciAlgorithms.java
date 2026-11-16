package recursion;

import core.Metrics;

/** Three ways to compute the n-th Fibonacci number. The counter records calls (or loop iterations). */
public final class FibonacciAlgorithms {
    /** Naive recursion makes about 1.6^n calls, so it is limited to a safe n. */
    public static final int MAX_NAIVE = 30;
    /** fib(92) is the largest Fibonacci number that fits in a 64-bit long. */
    public static final int MAX_FAST = 90;

    private FibonacciAlgorithms() { }

    /** Naive recursion. Purpose: show repeated work. Time O(2^n), space O(n) stack. */
    public static long naive(int n, Metrics m) {
        m.call();
        if (n <= 1) return n;
        return naive(n - 1, m) + naive(n - 2, m);
    }

    /** Memoized recursion: each value is computed once and stored. Time O(n), space O(n). */
    public static long memoized(int n, Metrics m) {
        long[] memo = new long[n + 1];
        for (int i = 0; i <= n; i++) memo[i] = -1;
        return memoized(n, memo, m);
    }

    private static long memoized(int n, long[] memo, Metrics m) {
        m.call();
        if (n <= 1) return n;
        if (memo[n] != -1) return memo[n];
        memo[n] = memoized(n - 1, memo, m) + memoized(n - 2, memo, m);
        return memo[n];
    }

    /** Bottom-up dynamic programming: builds the answer from fib(0) and fib(1). Time O(n), space O(1). */
    public static long bottomUp(int n, Metrics m) {
        if (n <= 1) return n;
        long previous = 0;
        long current = 1;
        for (int i = 2; i <= n; i++) {
            m.call(); // one loop iteration
            long next = previous + current;
            previous = current;
            current = next;
        }
        return current;
    }
}
