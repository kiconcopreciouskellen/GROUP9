package sorting;

import core.Metrics;
import java.util.Random;


 //Quicksort with the first element as pivot and a "yellow / green" partition, plus a randomized pivot option.
         //*Partition: the pivot sits at position low. Position i marks the end of the yellow region.
 //   yellow (low+1..i)   = values found to be smaller than the pivot
 //  green  (i+1..j-1)   = values not smaller than the pivot
 //  j     = the value being compared now
 //When j has passed the end, the pivot is swapped with position i, which puts it in its final place.
 // Moves = swaps.

public final class QuickSort {
    private QuickSort() { }


     // First-element pivot. Time complexity: best and average O(n log n), worst O(n^2) (already sorted or reverse sorted input).
     // Space complexity ;O(log n) average stack, O(n) worst.

    public static void sortFirstPivot(int[] a, Metrics m, SortTracer t) {
        sort(a, 0, a.length - 1, false, null, m, t);
    }


     //Random pivot: a random element is swapped to the front first, then the same partition is used.
     //Expected time O(n log n) for every input order. Space O(log n) expected.

    public static void sortRandomPivot(int[] a, Metrics m, SortTracer t) {
        sort(a, 0, a.length - 1, true, new Random(), m, t);
    }
// Recursively partitions  and sorts both sides of the placed pivot
    private static void sort(int[] a, int low, int high, boolean randomized, Random rnd, Metrics m, SortTracer t) {
        if (low >= high) return;
        int p = partition(a, low, high, randomized, rnd, m, t);
        sort(a, low, p - 1, randomized, rnd, m, t);
        sort(a, p + 1, high, randomized, rnd, m, t);
    }

    private static int partition(int[] a, int low, int high, boolean randomized, Random rnd, Metrics m, SortTracer t) {
        int n = a.length;
        //Swaps a random element into a[low] so the rest of the partition logic stays identical
        if (randomized) {
            int r = low + rnd.nextInt(high - low + 1);
            if (r != low) {
                swap(a, low, r);
                m.move();
            }
            if (t.enabled()) {
                byte[] k = t.marks(n);
                k[low] = SortTracer.PIVOT;
                if (r != low) k[r] = SortTracer.SWAP;
                t.frame(a, k, "Random pivot chosen (index " + r + "), moved to the front of positions " + low + ".." + high, false);
            }
        }
        int pivot = a[low];
        int i = low; // End of the yellow(<pivot) region
        for (int j = low + 1; j <= high; j++) {
            m.compare();
            boolean smaller = a[j] < pivot;
            if (t.enabled()) {
                t.frame(a, regions(t, n, low, high, i, j, j), "Compare " + a[j] + " with pivot " + pivot
                        + (smaller ? ": smaller, joins the yellow region" : ": not smaller, stays green"), false);
            }
            if (smaller) {
                i++;
                // Skip the self swap when no  green elements sit between  i and j yet
                if (i != j) {
                    swap(a, i, j);
                    m.move();
                }
                if (t.enabled()) {
                    byte[] k = regions(t, n, low, high, i, j + 1, -1);
                    k[i] = SortTracer.SWAP;
                    t.frame(a, k, "Yellow region grows to position " + i, false);
                }
            }
        }
        //This drops the pivot between the yellow & green pivots (if any smaller existed)
        if (i != low) {
            swap(a, low, i);
            m.move();
        }
        if (t.enabled()) {
            byte[] k = t.marks(n);
            for (int x = low; x < i; x++) k[x] = SortTracer.YELLOW;
            for (int x = i + 1; x <= high; x++) k[x] = SortTracer.GREEN;
            k[i] = SortTracer.SORTED;
            t.frame(a, k, "Pivot " + pivot + " placed at its final position " + i + " (yellow = smaller on the left, green = larger on the right)", true);
        }
        return i;
    }

    //Builds the marks for the partition: pivot, yellow region, green region, and the compared position.
    private static byte[] regions(SortTracer t, int n, int low, int high, int i, int nextUnseen, int compared) {
        byte[] k = t.marks(n);
        k[low] = SortTracer.PIVOT;
        for (int x = low + 1; x <= i; x++) k[x] = SortTracer.YELLOW;
        for (int x = i + 1; x < nextUnseen && x <= high; x++) k[x] = SortTracer.GREEN;
        if (compared >= 0) k[compared] = SortTracer.COMPARE;
        return k;
    }

    private static void swap(int[] a, int x, int y) {
        int tmp = a[x];
        a[x] = a[y];
        a[y] = tmp;
    }
}
