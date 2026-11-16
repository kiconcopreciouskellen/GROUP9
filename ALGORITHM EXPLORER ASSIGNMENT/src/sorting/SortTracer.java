package sorting;

//Records snapshots ("frames") of the array while a sort runs, so the interface can replay the sort step by step visually.

 //A frame holds the array values, a color mark for every position, and a short note.

 //SortTracer.OFF is used during timing benchmarks ,it  records with zero overhead, so timing runs use it.

public class SortTracer {

    //Visual highlight states for each array element
    public static final byte NONE = 0;
    public static final byte COMPARE = 1;
    public static final byte SWAP = 2;
    public static final byte SORTED = 3;
    public static final byte PIVOT = 4;
    public static final byte YELLOW = 5;   // quicksort: elements already found smaller than the pivot
    public static final byte GREEN = 6;    // quicksort: elements not yet moved, still to be compared / larger side
       //Shared instance for timing runs
    public static final SortTracer OFF = new SortTracer(false);

    //One snapshot of the array at one step of the sort.
    public static final class Frame {
        public final int[] values;
        public final byte[] marks;
        public final String note;
        public final boolean passEnd;

        Frame(int[] values, byte[] marks, String note, boolean passEnd) {
            this.values = values;
            this.marks = marks;
            this.note = note;
            this.passEnd = passEnd;
        }
    }

    private final boolean on;
    private Frame[] frames = new Frame[64];
    private int count;

    public SortTracer(boolean on) {
        this.on = on;
    }

    public boolean enabled() {
        return on;
    }

    // A fresh mark array (all NONE) for an array of length n.
    public byte[] marks(int n) {
        return new byte[n];
    }

    //Saves a snapshot of the current state of the array
    //Set passEnd to true when finishing a major step like a full pass or merge
    public void frame(int[] a, byte[] marks, String note, boolean passEnd) {
        if (!on) return;
        //Copy the array so future swaps don't change past frames
        int[] copy = new int[a.length];
        for (int i = 0; i < a.length; i++) copy[i] = a[i];
        //Grow the buffer if we run out of space
        if (count == frames.length) {
            Frame[] bigger = new Frame[frames.length * 2];
            for (int i = 0; i < count; i++) bigger[i] = frames[i];
            frames = bigger;
        }
        frames[count++] = new Frame(copy, marks, note, passEnd);
    }

    public int size() {
        return count;
    }

    public Frame get(int index) {
        return frames[index];
    }
}
