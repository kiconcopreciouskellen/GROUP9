package sorting;

import core.ArrayUtil;
import core.OperationCounter;
import core.StepLogger;

public final class BubbleSort {
    private BubbleSort() { }

    public static void sort(int[] a, OperationCounter ops, StepLogger log) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            if (log.enabled()) log.log("Pass " + (i + 1) + ":");
            for (int j = 0; j < n - i - 1; j++) {
                ops.increment(); 
                if (log.enabled()) {
                    log.log("  Compare index " + j + " (" + a[j] + ") and index " + (j + 1) + " (" + a[j + 1] + ")");
                }
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    ops.increment(); 
                    swapped = true;
                    if (log.enabled()) log.log("    swap -> " + ArrayUtil.format(a));
                }
            }
            if (!swapped) {
                if (log.enabled()) log.log("  No swaps in this pass: the array is sorted, stopping early.");
                break;
            }
        }
    }
}
