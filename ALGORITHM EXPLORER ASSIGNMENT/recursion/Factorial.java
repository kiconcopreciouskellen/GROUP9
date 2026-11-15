package recursion;

import core.OperationCounter;
import core.StepLogger;

/** Recursive factorial. Operation = one recursive call. */
public final class Factorial {
    /** 21! overflows a long so larger inputs are rejected instead of silently wrapping around. */
    public static final int MAX_N = 20;

    private Factorial() { }

    public static long compute(int n, OperationCounter ops, StepLogger log) {
        if (n < 0) throw new IllegalArgumentException("Factorial needs a non-negative integer.");
        if (n > MAX_N) {
            throw new IllegalArgumentException("n must be at most " + MAX_N + ", because " + (MAX_N + 1)
                    + "! does not fit in a 64-bit long.");
        }
        return compute(n, 1, ops, log);
    }

    private static long compute(int n, int depth, OperationCounter ops, StepLogger log) {
        ops.increment();
        if (log.enabled()) log.log("Call depth " + depth + ": factorial(" + n + ")");
        if (n <= 1) {
            if (log.enabled()) log.log("  base case reached, return 1");
            return 1;
        }
        long sub = compute(n - 1, depth + 1, ops, log);
        long result = n * sub;
        if (log.enabled()) log.log("  depth " + depth + ": " + n + " * " + sub + " = " + result);
        return result;
    }
}
