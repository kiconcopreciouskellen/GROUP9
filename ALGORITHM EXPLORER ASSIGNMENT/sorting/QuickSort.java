package sorting;

import core.ArrayUtil;
import core.OperationCounter;
import core.StepLogger;


public final class QuickSort {
    private QuickSort() { }

    public static void sort(int[] a, OperationCounter ops, StepLogger log) {
        sort(a, 0, a.length - 1, ops, log);
    }

    private static void sort(int[] a, int low, int high, OperationCounter ops, StepLogger log) {
        if (low < high) {
            int p = partition(a, low, high, ops, log);
            sort(a, low, p - 1, ops, log);
            sort(a, p + 1, high, ops, log);
        }
    }

    private static int partition(int[] a, int low, int high, OperationCounter ops, StepLogger log) {
        int mid = low + (high - low) / 2;
        if (mid != high) {
            swap(a, mid, high);
            ops.increment(); // swap pivot to the end
        }
        int pivot = a[high];
        if (log.enabled()) {
            log.log("Sub-array [" + low + ".." + high + "], pivot = " + pivot + " (middle element moved to index " + high + ")");
        }
        int i = low - 1;
        for (int j = low; j < high; j++) {
            ops.increment(); // comparison
            if (a[j] < pivot) {
                i++;
                if (i != j) {
                    swap(a, i, j);
                    ops.increment(); // swap
                    if (log.enabled()) log.log("  swap index " + i + " and " + j + " -> " + ArrayUtil.format(a));
                }
            }
        }
        if (i + 1 != high) {
            swap(a, i + 1, high);
            ops.increment(); // swap pivot into place
        }
        if (log.enabled()) log.log("  pivot placed at index " + (i + 1) + " -> " + ArrayUtil.format(a));
        return i + 1;
    }

    private static void swap(int[] a, int x, int y) {
        int t = a[x];
        a[x] = a[y];
        a[y] = t;
    }
}
