package sorting;

import core.Metrics;

//Insertion sort using adjacent swaps. Moves = swaps (one per inversion removed).
public final class InsertionSort {
    private InsertionSort() { }

    //Time complexity: best case O(n) (already sorted), average and worst case O(n^2). Space O(1). Stable. */
    public static void sort(int[] a, Metrics m, SortTracer t) {
        int n = a.length;
        //Index 0 is simply sorted; pick each subsequent element and insert it into the sorted prefix
        for (int i = 1; i < n; i++) {
            int j = i;
            //Waks a[j] leftwards through the sorted prefix until it finds it spot
            while (j > 0) {
                m.compare();
                boolean outOfOrder = a[j - 1] > a[j];
                if (t.enabled()) {
                    byte[] k = prefix(t, n, i + 1);
                    k[j - 1] = SortTracer.COMPARE;
                    k[j] = SortTracer.COMPARE;
                    t.frame(a, k, "Compare " + a[j - 1] + " and " + a[j] + (outOfOrder ? ": shift left" : ": in place"), false);
                }
                //Prefix is already ordered, so there is no need to continue scanning left
                if (!outOfOrder) break;
                int tmp = a[j - 1];
                a[j - 1] = a[j];
                a[j] = tmp;
                m.move();
                if (t.enabled()) {
                    byte[] k = prefix(t, n, i + 1);
                    k[j - 1] = SortTracer.SWAP;
                    k[j] = SortTracer.SWAP;
                    t.frame(a, k, "Swapped", false);
                }
                j--;
            }
            if (t.enabled()) {
                t.frame(a, prefix(t, n, i + 1), "End of pass " + i + ": the first " + (i + 1) + " values are sorted", true);
            }
        }
    }

    // Marks positions 0..end-1 as the sorted part.
    private static byte[] prefix(SortTracer t, int n, int end) {
        byte[] k = t.marks(n);
        for (int i = 0; i < end; i++) k[i] = SortTracer.SORTED;
        return k;
    }
}
