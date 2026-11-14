package core;

/** Input parsing and display helpers shared by the GUI and the algorithms. */
public final class ArrayUtil {
    private static final int DISPLAY_LIMIT = 100;

    private ArrayUtil() { }

    /** Parses "1, 2, 3" into an int array and accounts for error conditions. */
    public static int[] parse(String text, int maxElements) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input is empty. Enter integers separated by commas.");
        }
        String[] parts = text.split(",", -1);
        if (parts.length > maxElements) {
            throw new IllegalArgumentException("Too many values (" + parts.length
                    + "). The maximum for this algorithm is " + maxElements + ".");
        }
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.isEmpty()) {
                throw new IllegalArgumentException("Empty value at position " + (i + 1)
                        + ". Check for doubled or trailing commas.");
            }
            try {
                result[i] = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + p + "' is not a valid integer.");
            }
        }
        return result;
    }

    /** Parses a single integer field. */
    public static int parseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a whole number, but was '" + text.trim() + "'.");
        }
    }

    /** Formats an array as [a, b, c]. the long arrays show only the first 100 values. */
    public static String format(int[] a) {
        StringBuilder sb = new StringBuilder("[");
        int shown = Math.min(a.length, DISPLAY_LIMIT);
        for (int i = 0; i < shown; i++) {
            sb.append(a[i]);
            if (i < shown - 1) sb.append(", ");
        }
        if (a.length > shown) sb.append(", ... (+").append(a.length - shown).append(" more)");
        sb.append("]");
        return sb.toString();
    }

    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) return false;
        }
        return true;
    }
}
