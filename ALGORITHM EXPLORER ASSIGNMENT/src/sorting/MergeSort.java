package sorting;

import core.Metrics;

//Top-down merge sort. Moves = element writes back into the main array.
public final class MergeSort {
    private MergeSort() { }

    // Time complexity: best, average and worst case O(n log n). Space  complexityO(n).
    public static void sort(int[] a, Metrics m, SortTracer t) {
        sort(a, 0, a.length - 1, m, t);
    }

    //Recursively Splits a[l--r] into halves and merges them in sorted order
    private static void sort(int[] a, int l, int r, Metrics m, SortTracer t) {
        if (l >= r) return;
        int mid = l + (r - l) / 2;
        sort(a, l, mid, m, t);
        sort(a, mid + 1, r, m, t);
        merge(a, l, mid, r, m, t);
    }

//Merges the two adjacent sorted sub arrays
    private static void merge(int[] a, int l, int mid, int r, Metrics m, SortTracer t) {
        int n1 = mid - l + 1;
        int n2 = r - mid;
        //Copies both halves into scratch arrays so we don't  overwrite unread values in a[]
        int[] left = new int[n1];
        int[] right = new int[n2];
        for (int x = 0; x < n1; x++) left[x] = a[l + x];
        for (int y = 0; y < n2; y++) right[y] = a[mid + 1 + y];

        int i = 0, j = 0, k = l;
        //Picks the  smallest front element from left to right
        while (i < n1 && j < n2) {
            m.compare();
            if (left[i] <= right[j]) {
                a[k++] = left[i++];
            } else {
                a[k++] = right[j++];
            }
            m.move();
        }
        //flushes any leftover elements once a half is exhausted
        while (i < n1) {
            a[k++] = left[i++];
            m.move();
        }
        while (j < n2) {
            a[k++] = right[j++];
            m.move();
        }
        if (t.enabled()) {
            byte[] marks = t.marks(a.length);
            for (int x = l; x <= r; x++) marks[x] = SortTracer.SWAP;
            t.frame(a, marks, "Merged positions " + l + ".." + r + " (left half " + l + ".." + mid
                    + " and right half " + (mid + 1) + ".." + r + ")", true);
        }
    }
}
