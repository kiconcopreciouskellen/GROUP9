package core;

/**
 * Runs a task on a thread with a very large call stack.
 * First-element quicksort on sorted input recurses n levels deep, which overflows the default stack
 * for n in the thousands. Running the sorts here avoids that.
 */
public final class BigStack {
    private static final long STACK_BYTES = 256L * 1024 * 1024;

    private BigStack() { }

    public static void run(Runnable task) {
        final Throwable[] failure = new Throwable[1];
        Thread worker = new Thread(null, () -> {
            try {
                task.run();
            } catch (Throwable t) {
                failure[0] = t;
            }
        }, "big-stack-worker", STACK_BYTES);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (failure[0] instanceof RuntimeException) throw (RuntimeException) failure[0];
        if (failure[0] instanceof Error) throw (Error) failure[0];
    }
}
