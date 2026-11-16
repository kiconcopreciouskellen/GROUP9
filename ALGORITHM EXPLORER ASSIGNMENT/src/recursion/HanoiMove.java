package recursion;

/** One move in Tower of Hanoi: which disk goes from which peg to which peg. */
public final class HanoiMove {
    public final int disk;
    public final char from;
    public final char to;

    public HanoiMove(int disk, char from, char to) {
        this.disk = disk;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Move disk " + disk + " from " + from + " to " + to;
    }
}
