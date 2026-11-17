package sorting;

import core.Metrics;

//The list of sorting algorithms offered by the application, with a single entry point and their complexity facts.
public final class SortRegistry {
    //Display names used across the UI and registry lookups
    public static final String BUBBLE = "Bubble Sort";
    public static final String SELECTION = "Selection Sort";
    public static final String INSERTION = "Insertion Sort";
    public static final String MERGE = "Merge Sort";
    public static final String QUICK_FIRST = "Quick Sort (first pivot)";
    public static final String QUICK_RANDOM = "Quick Sort (random pivot)";
//Ordered list of all available algorithms
    public static final String[] NAMES = {BUBBLE, SELECTION, INSERTION, MERGE, QUICK_FIRST, QUICK_RANDOM};

    private SortRegistry() { }

    //Sorts the array in place with the named algorithm. When the tracer is on, a final "sorted" frame is added.
    public static void sort(String name, int[] a, Metrics m, SortTracer t) {
        switch (name) {
            case BUBBLE:       BubbleSort.sort(a, m, t); break;
            case SELECTION:    SelectionSort.sort(a, m, t); break;
            case INSERTION:    InsertionSort.sort(a, m, t); break;
            case MERGE:        MergeSort.sort(a, m, t); break;
            case QUICK_FIRST:  QuickSort.sortFirstPivot(a, m, t); break;
            case QUICK_RANDOM: QuickSort.sortRandomPivot(a, m, t); break;
            default: throw new IllegalArgumentException("Unknown algorithm: " + name);
        }
        //Append a uniform "all green/sorted" closing frame so the visualizer ends cleanly
        if (t.enabled()) {
            byte[] k = t.marks(a.length);
            for (int i = 0; i < k.length; i++) k[i] = SortTracer.SORTED;
            t.frame(a, k, "The array is sorted", true);
        }
    }

    // Returns{best, average, worst, extra space, stable} for the named algorithm.
    public static String[] facts(String name) {
        switch (name) {
            case BUBBLE:       return new String[]{"O(n)", "O(n^2)", "O(n^2)", "O(1)", "Yes"};
            case SELECTION:    return new String[]{"O(n^2)", "O(n^2)", "O(n^2)", "O(1)", "No"};
            case INSERTION:    return new String[]{"O(n)", "O(n^2)", "O(n^2)", "O(1)", "Yes"};
            case MERGE:        return new String[]{"O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Yes"};
            case QUICK_FIRST:  return new String[]{"O(n log n)", "O(n log n)", "O(n^2)", "O(log n) avg, O(n) worst", "No"};
            case QUICK_RANDOM: return new String[]{"O(n log n)", "O(n log n) expected", "O(n^2) (very unlikely)", "O(log n) expected", "No"};
            default: throw new IllegalArgumentException("Unknown algorithm: " + name);
        }
    }

    // Explains  what the move counter means for the named algorithm.
    public static String moveMeaning(String name) {
        switch (name) {
            case MERGE: return "writes into the array";
            default:    return "swaps";
        }
    }
}
