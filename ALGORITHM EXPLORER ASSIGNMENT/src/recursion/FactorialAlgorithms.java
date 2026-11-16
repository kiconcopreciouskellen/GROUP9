package recursion;

import core.Metrics;

/** Recursive and iterative factorial. The counter records calls (or loop iterations). */
public final class FactorialAlgorithms {
    /** 21! does not fit in a 64-bit long. */
    public static final int MAX_N = 20;

    private FactorialAlgorithms() { }

    /** Rejects negative input and values whose factorial would overflow. n = 0 is valid (0! = 1). */
    public static void validate(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        if (n > MAX_N) {
            throw new IllegalArgumentException("n must be at most " + MAX_N + ", because " + (MAX_N + 1)
                    + "! does not fit in a 64-bit integer.");
        }
    }

    /** Recursive factorial. Time O(n), space O(n) stack. */
    public static long recursive(int n, Metrics m) {
        validate(n);
        return recursiveStep(n, m);
    }

    private static long recursiveStep(int n, Metrics m) {
        m.call();
        if (n <= 1) return 1;
        return n * recursiveStep(n - 1, m);
    }

    /** Iterative factorial. Time O(n), space O(1). */
    public static long iterative(int n, Metrics m) {
        validate(n);
        long result = 1;
        for (int i = 2; i <= n; i++) {
            m.call(); // one loop iteration
            result *= i;
        }
        return result;
    }
}
