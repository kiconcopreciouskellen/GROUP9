package analysis;

import core.BigStack;
import core.Metrics;
import java.util.Random;
import sorting.SortRegistry;
import sorting.SortTracer;


public final class Benchmark {
    public static final int[] SIZES = {100, 500, 1000, 2000, 5000, 10000};
    public static final int[] LARGE_SIZES = {50000, 100000};
    public static final String[] LARGE_ALGORITHMS = {SortRegistry.MERGE, SortRegistry.QUICK_FIRST, SortRegistry.QUICK_RANDOM};
    public static final int REPEATS = 3;

    private Benchmark() { }

    //Receives progress updates while the experiment runs.
    public interface Progress {
        void update(int done, int total, String message);
    }

    public static final class Results {
        public final double[][][] timeMs = new double[InputGenerator.KINDS.length][SortRegistry.NAMES.length][SIZES.length];
        public final double[][][] comparisons = new double[InputGenerator.KINDS.length][SortRegistry.NAMES.length][SIZES.length];
        public final double[][][] moves = new double[InputGenerator.KINDS.length][SortRegistry.NAMES.length][SIZES.length];
        public final double[][] largeTimeMs = new double[LARGE_ALGORITHMS.length][LARGE_SIZES.length];
    }

    // Runs the whole experiment with a large-stack thread and returns the results. 
    public static Results run(Progress progress) {
        final Results[] holder = new Results[1];
        BigStack.run(() -> holder[0] = runExperiment(progress));
        return holder[0];
    }

    private static Results runExperiment(Progress progress) {
        Results r = new Results();
        int kinds = InputGenerator.KINDS.length;
        int algos = SortRegistry.NAMES.length;
        int total = kinds * SIZES.length * algos + LARGE_ALGORITHMS.length * LARGE_SIZES.length;
        int done = 0;

        warmUp();

        for (int k = 0; k < kinds; k++) {
            for (int s = 0; s < SIZES.length; s++) {
                int n = SIZES[s];
                for (int a = 0; a < algos; a++) {
                    double time = 0, cmp = 0, mov = 0;
                    for (int rep = 0; rep < REPEATS; rep++) {
                        int[] input = InputGenerator.make(InputGenerator.KINDS[k], n, new Random(1000L * n + rep));
                        Metrics m = new Metrics();
                        long start = System.nanoTime();
                        SortRegistry.sort(SortRegistry.NAMES[a], input, m, SortTracer.OFF);
                        time += (System.nanoTime() - start) / 1_000_000.0;
                        cmp += m.getComparisons();
                        mov += m.getMoves();
                    }
                    r.timeMs[k][a][s] = time / REPEATS;
                    r.comparisons[k][a][s] = cmp / REPEATS;
                    r.moves[k][a][s] = mov / REPEATS;
                    done++;
                    progress.update(done, total, InputGenerator.KINDS[k] + ", n = " + n + ", " + SortRegistry.NAMES[a]);
                }
            }
        }

        for (int a = 0; a < LARGE_ALGORITHMS.length; a++) {
            for (int s = 0; s < LARGE_SIZES.length; s++) {
                double time = 0;
                for (int rep = 0; rep < REPEATS; rep++) {
                    int[] input = InputGenerator.random(LARGE_SIZES[s], new Random(7000L + rep), 1_000_000);
                    long start = System.nanoTime();
                    SortRegistry.sort(LARGE_ALGORITHMS[a], input, new Metrics(), SortTracer.OFF);
                    time += (System.nanoTime() - start) / 1_000_000.0;
                }
                r.largeTimeMs[a][s] = time / REPEATS;
                done++;
                progress.update(done, total, "Random, n = " + LARGE_SIZES[s] + ", " + LARGE_ALGORITHMS[a]);
            }
        }
        return r;
    }

    private static void warmUp() {
        Random rnd = new Random(1);
        for (String name : SortRegistry.NAMES) {
            for (int i = 0; i < 3; i++) {
                SortRegistry.sort(name, InputGenerator.random(1500, rnd, 100000), new Metrics(), SortTracer.OFF);
            }
        }
    }

    public static String toCsv(Results r) {
        StringBuilder sb = new StringBuilder("input,algorithm,n,avg_time_ms,avg_comparisons,avg_moves\n");
        for (int k = 0; k < InputGenerator.KINDS.length; k++) {
            for (int a = 0; a < SortRegistry.NAMES.length; a++) {
                for (int s = 0; s < SIZES.length; s++) {
                    sb.append(InputGenerator.KINDS[k]).append(',').append(SortRegistry.NAMES[a]).append(',')
                      .append(SIZES[s]).append(',').append(String.format("%.4f", r.timeMs[k][a][s])).append(',')
                      .append(Math.round(r.comparisons[k][a][s])).append(',')
                      .append(Math.round(r.moves[k][a][s])).append('\n');
                }
            }
        }
        for (int a = 0; a < LARGE_ALGORITHMS.length; a++) {
            for (int s = 0; s < LARGE_SIZES.length; s++) {
                sb.append(InputGenerator.RANDOM).append(',').append(LARGE_ALGORITHMS[a]).append(',')
                  .append(LARGE_SIZES[s]).append(',').append(String.format("%.4f", r.largeTimeMs[a][s])).append(",,\n");
            }
        }
        return sb.toString();
    }
}
