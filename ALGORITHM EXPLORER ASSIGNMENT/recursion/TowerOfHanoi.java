package recursion;

import core.OperationCounter;
import core.StepLogger;

/** Tower of Hanoi. Operation = one recursive call, which equals one disk move (2^n - 1 in total). */
public final class TowerOfHanoi {
    public static final int MAX_DISKS = 20;

    private TowerOfHanoi() { }

    /** Returns the number of moves. Pegs are A (source), B (spare) and C (target). */
    public static long solve(int disks, OperationCounter ops, StepLogger log) {
        if (disks < 1) throw new IllegalArgumentException("Number of disks must be at least 1.");
        if (disks > MAX_DISKS) throw new IllegalArgumentException("Number of disks must be at most " + MAX_DISKS + ".");
        return move(disks, 'A', 'C', 'B', ops, log);
    }

    private static long move(int n, char from, char to, char via, OperationCounter ops, StepLogger log) {
        ops.increment();
        if (n == 1) {
            if (log.enabled()) log.log("Move disk 1 from " + from + " to " + to);
            return 1;
        }
        long moves = move(n - 1, from, via, to, ops, log);
        if (log.enabled()) log.log("Move disk " + n + " from " + from + " to " + to);
        moves++;
        moves += move(n - 1, via, to, from, ops, log);
        return moves;
    }
}
