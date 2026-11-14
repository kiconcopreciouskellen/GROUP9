package core;

/** Single source of truth for the category and algorithm names shown in the GUI. */
public final class AlgorithmNames {
    private AlgorithmNames() { }

    public static final String SORTING = "Sorting";
    public static final String SEARCHING = "Searching";
    public static final String RECURSION = "Recursion";
    public static final String DATA_STRUCTURES = "Data Structures";
    public static final String ANALYSIS = "Complexity Analysis";

    public static final String BUBBLE_SORT = "Bubble Sort";
    public static final String QUICK_SORT = "Quick Sort";
    public static final String MERGE_SORT = "Merge Sort";

    public static final String BINARY_SEARCH = "Binary Search";
    public static final String LINEAR_SEARCH = "Linear Search";

    public static final String FACTORIAL = "Factorial (Recursive)";
    public static final String FIB_NAIVE = "Fibonacci (Naive Recursion)";
    public static final String FIB_MEMO = "Fibonacci (Memoized)";
    public static final String HANOI = "Tower of Hanoi";

    public static final String STACK = "Custom Stack (LIFO)";
    public static final String QUEUE = "Custom Queue (FIFO)";
    public static final String LINKED_LIST = "Custom Singly Linked List";
    public static final String BST = "Custom Binary Search Tree (BST)";

    public static final String BENCHMARK = "Benchmark All Algorithms";

    public static final String[] CATEGORIES = {SORTING, SEARCHING, RECURSION, DATA_STRUCTURES, ANALYSIS};

    public static String[] algorithmsFor(String category) {
        switch (category) {
            case SORTING:         return new String[]{BUBBLE_SORT, QUICK_SORT, MERGE_SORT};
            case SEARCHING:       return new String[]{BINARY_SEARCH, LINEAR_SEARCH};
            case RECURSION:       return new String[]{FACTORIAL, FIB_NAIVE, FIB_MEMO, HANOI};
            case DATA_STRUCTURES: return new String[]{STACK, QUEUE, LINKED_LIST, BST};
            case ANALYSIS:        return new String[]{BENCHMARK};
            default:              return new String[0];
        }
    }
}
