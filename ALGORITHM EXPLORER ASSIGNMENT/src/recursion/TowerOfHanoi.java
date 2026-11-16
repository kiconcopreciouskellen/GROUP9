package recursion;

import core.Metrics;

/** Recursive Tower of Hanoi solver. Produces the full list of moves (2^n - 1 of them). */
public final class TowerOfHanoi {
    public static final int MAX_DISKS = 10;

    private TowerOfHanoi() { }

    /**
     * Solves the puzzle for the chosen pegs. Time O(2^n), space O(n) stack.
     * The counter records recursive calls, which equals the number of moves.
     */
    public static HanoiMove[] solve(int disks, char source, char destination, char auxiliary, Metrics m) {
        if (disks < 1 || disks > MAX_DISKS) {
            throw new IllegalArgumentException("Number of disks must be between 1 and " + MAX_DISKS + ".");
        }
        if (source == destination || source == auxiliary || destination == auxiliary) {
            throw new IllegalArgumentException("Source, destination and auxiliary must be three different pegs.");
        }
        HanoiMove[] moves = new HanoiMove[(1 << disks) - 1];
        solve(disks, source, destination, auxiliary, moves, new int[1], m);
        return moves;
    }

    private static void solve(int n, char from, char to, char via, HanoiMove[] moves, int[] next, Metrics m) {
        m.call();
        if (n == 1) {
            moves[next[0]++] = new HanoiMove(1, from, to);
            return;
        }
        solve(n - 1, from, via, to, moves, next, m);
        moves[next[0]++] = new HanoiMove(n, from, to);
        solve(n - 1, via, to, from, moves, next, m);
    }
}
