package core;

/** High-resolution timing helper (System.nanoTime). */
public final class Timing {
    private Timing() { }

    /** Runs the task once untimed (warm-up), then returns the average time in milliseconds over the given repeats. */
    public static double averageMillis(int repeats, Runnable task) {
        task.run();
        long total = 0;
        for (int i = 0; i < repeats; i++) {
            long start = System.nanoTime();
            task.run();
            total += System.nanoTime() - start;
        }
        return total / (double) repeats / 1_000_000.0;
    }

    public static String format(double millis) {
        return String.format("%.4f ms", millis);
    }
}
