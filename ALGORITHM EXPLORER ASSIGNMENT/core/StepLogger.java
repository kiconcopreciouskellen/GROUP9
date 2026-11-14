package core;

/**
 * Receives the step-by-step log lines produced by the algorithms.
 * The algorithms never touch the GUI; the interface supplies an implementation.
 */
public interface StepLogger {

    void log(String line);

    /** Algorithms check this before building log strings, so silent (timed) runs pay no logging cost. */
    default boolean enabled() {
        return true;
    }

    /** A logger that discards everything. Used for timing and benchmarking. */
    StepLogger NONE = new StepLogger() {
        @Override
        public void log(String line) { }

        @Override
        public boolean enabled() {
            return false;
        }
    };
}
