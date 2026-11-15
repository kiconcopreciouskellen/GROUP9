package sorting;

import core.ArrayUtil;
import core.OperationCounter;
import core.StepLogger;

/** Top-down merge sort. Operation = one comparison or one write back into the main array. */
public final class MergeSort {
    private MergeSort() { }

    public static void sort(int[] a, OperationCounter ops, StepLogger log) {
        sort(a, 0, a.length - 1, ops, log);
    }

    private static void sort(int[] a, int l, int r, OperationCounter ops, StepLogger log) {
        if (l >= r) return;
        int m = l + (r - l) / 2;
        if (log.enabled()) {
            log.log("Split [" + l + ".." + r + "] into [" + l + ".." + m + "] and [" + (m + 1) + ".." + r + "]");
        }
        sort(a, l, m, ops, log);
        sort(a, m + 1, r, ops, log);
        merge(a, l, m, r, ops, log);
    }

    private static void merge(int[] a, int l, int m, int r, OperationCounter ops, StepLogger log) {
        int n1 = m - l + 1;
        int n2 = r - m;
        int[] left = new int[n1];
        int[] right = new int[n2];
        for (int x = 0; x < n1; x++) left[x] = a[l + x];
        for (int y = 0; y < n2; y++) right[y] = a[m + 1 + y];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            ops.increment(); // comparison
            if (left[i] <= right[j]) {
                a[k++] = left[i++];
            } else {
                a[k++] = right[j++];
            }
            ops.increment(); // write
        }
        while (i < n1) {
            a[k++] = left[i++];
            ops.increment(); // write
        }
        while (j < n2) {
            a[k++] = right[j++];
            ops.increment(); // write
        }
        if (log.enabled()) log.log("Merged [" + l + ".." + r + "] -> " + ArrayUtil.format(a));
    }
}
