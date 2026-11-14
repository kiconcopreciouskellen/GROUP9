package core;

/** Counts the basic operations performed by an algorithm. Each module documents what it counts. */
public class OperationCounter {
    private long count;

    public void increment() {
        count++;
    }

    public void add(long n) {
        count += n;
    }

    public long get() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}
