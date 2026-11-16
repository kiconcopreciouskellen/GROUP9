package analysis;

import java.util.Random;

public final class InputGenerator {
    public static final String RANDOM = "Random";
    public static final String SORTED = "Already sorted";
    public static final String REVERSE = "Reverse sorted";
    public static final String[] KINDS = {RANDOM, SORTED, REVERSE};

    private InputGenerator() { }

    public static int[] random(int n, Random rnd, int bound) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt(bound);
        return a;
    }

    public static int[] sorted(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;
    }

    public static int[] reverse(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    public static int[] make(String kind, int n, Random rnd) {
        switch (kind) {
            case SORTED:  
            return sorted(n);
            case REVERSE: 
            return reverse(n);
            default:      
            return random(n, rnd, 1_000_000);
        }
    }
}
