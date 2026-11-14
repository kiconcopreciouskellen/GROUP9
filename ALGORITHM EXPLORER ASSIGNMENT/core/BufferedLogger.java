package core;

/** Collects log lines in memory, up to a maximum number of lines, so very large runs do not freeze the GUI. */
public class BufferedLogger implements StepLogger {
    private final StringBuilder buffer = new StringBuilder();
    private final int maxLines;
    private int lines;
    private boolean truncated;

    public BufferedLogger(int maxLines) {
        this.maxLines = maxLines;
    }

    @Override
    public void log(String line) {
        if (lines < maxLines) {
            buffer.append(line).append('\n');
            lines++;
        } else if (!truncated) {
            truncated = true;
            buffer.append("... log truncated after ").append(maxLines)
                  .append(" lines (the operation count still covers the whole run) ...\n");
        }
    }

    public String getText() {
        return buffer.toString();
    }
}
