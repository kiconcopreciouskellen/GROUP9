package sorting;

import core.Metrics;

// Selection sort. Moves = swaps.
public final class SelectionSort {
    private SelectionSort() { }

    //Time Complexity: best, average and worst O(n^2). Space O(1). Not stable (the long-distance swap can reorder equal values).
    public static void sort(int[] a, Metrics m, SortTracer t) {
        int n = a.length;
        //Grows the sorted prefix from left to right ; the last element will automatically fall into place
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            //Scans the unsorted remainder to locate the smallest value
            for (int j = i + 1; j < n; j++) {
                m.compare();
                if (t.enabled()) {
                    byte[] k = prefix(t, n, i);
                    k[min] = SortTracer.PIVOT;
                    k[j] = SortTracer.COMPARE;
                    t.frame(a, k, "Compare " + a[j] + " with the smallest so far (" + a[min] + ")", false);
                }
                if (a[j] < a[min]) min = j;
            }
            //Skips the self swap if the index i was already the minimum
            if (min != i) {
                int tmp = a[i];
                a[i] = a[min];
                a[min] = tmp;
                m.move();
                if (t.enabled()) {
                    byte[] k = prefix(t, n, i);
                    k[i] = SortTracer.SWAP;
                    k[min] = SortTracer.SWAP;
                    t.frame(a, k, "Swap the smallest value into position " + i, false);
                }
            }
            if (t.enabled()) {
                t.frame(a, prefix(t, n, i + 1), "End of pass " + (i + 1) + ": position " + i + " is final", true);
            }
        }
    }

    // Marks positions 0..end-1 as sorted.
    private static byte[] prefix(SortTracer t, int n, int end) {
        byte[] k = t.marks(n);
        for (int i = 0; i < end; i++) k[i] = SortTracer.SORTED;
        return k;
    }
}
