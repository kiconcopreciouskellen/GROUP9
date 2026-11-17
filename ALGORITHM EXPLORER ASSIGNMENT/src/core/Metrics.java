package core;

/**
 * Counts the basic operations of one algorithm run.
 * comparisons: element comparisons.
 * moves: swaps or element writes (each module states which it counts).
 * calls: recursive function calls or loop iterations (used by the recursion module).
 */
public class Metrics {
    private long comparisons;
    private long moves;
    private long calls;

    public void compare() { comparisons++; }
    public void move() { moves++; }
    public void call() { calls++; }

    public long getComparisons() { return comparisons; }
    public long getMoves() { return moves; }
    public long getCalls() { return calls; }

    public void reset() {
        comparisons = 0;
        moves = 0;
        calls = 0;
    }
}
