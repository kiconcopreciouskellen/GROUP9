package sorting;

import core.Metrics;

//Bubble sort with early exit. Moves = swaps.
public final class BubbleSort {
    private BubbleSort() { }

    //Time Complexity : best O(n) (already sorted), average and worst O(n^2). Space O(1).
    public static void sort(int[] a, Metrics m, SortTracer t) {
        int n = a.length;
        //each pass bubbles the next largest element into its final spot at the end
        for (int pass = 1; pass < n; pass++) {
            boolean swapped = false;
            //Only sans the sorted prefix;everything from(n-pass) onward is locked in
            for (int j = 0; j < n - pass; j++) {
                m.compare();
                boolean outOfOrder = a[j] > a[j + 1];
                if (t.enabled()) {
                    byte[] k = tail(t, n, n - pass + 1);
                    k[j] = SortTracer.COMPARE;
                    k[j + 1] = SortTracer.COMPARE;
                    t.frame(a, k, "Compare " + a[j] + " and " + a[j + 1] + (outOfOrder ? ": out of order, swap" : ": in order"), false);
                }
                if (outOfOrder) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    m.move();
                    swapped = true;
                    if (t.enabled()) {
                        byte[] k = tail(t, n, n - pass + 1);
                        k[j] = SortTracer.SWAP;
                        k[j + 1] = SortTracer.SWAP;
                        t.frame(a, k, "Swapped", false);
                    }
                }
            }
            if (t.enabled()) {
                t.frame(a, tail(t, n, n - pass), "End of pass " + pass + ": the largest remaining value is in place", true);
            }
            //Early exit;If nothing moved this pass then the array is already sorted
            if (!swapped) break;
        }
    }

    //Marks positions from n-1 as sorted.
    private static byte[] tail(SortTracer t, int n, int from) {
        byte[] k = t.marks(n);
        for (int i = Math.max(from, 0); i < n; i++) k[i] = SortTracer.SORTED;
        return k;
    }
}
