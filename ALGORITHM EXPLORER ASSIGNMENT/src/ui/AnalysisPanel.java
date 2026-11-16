package ui;

import analysis.Benchmark;
import analysis.InputGenerator;
import sorting.SortRegistry;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


class AnalysisPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String[] QUADRATIC = {SortRegistry.BUBBLE, SortRegistry.SELECTION, SortRegistry.INSERTION};
    private static final String[] LINEARITHMIC = {SortRegistry.MERGE, SortRegistry.QUICK_FIRST, SortRegistry.QUICK_RANDOM};

    private final JButton runButton = UiUtil.button("Run experiment");
    private final JButton csvButton = UiUtil.button("Export CSV");
    private final JButton pngButton = UiUtil.button("Save charts as PNG");
    private final JProgressBar progress = new JProgressBar(0, 100);
    private final JLabel message = UiUtil.messageLabel();
    private final JPanel tablesPanel = new JPanel();
    private final JComboBox<String> kindBox = new JComboBox<>(InputGenerator.KINDS);
    private final LineChartPanel quadraticChart = new LineChartPanel("O(n^2) sorts: running time against n");
    private final LineChartPanel linearithmicChart = new LineChartPanel("O(n log n) sorts: running time against n");
    private transient Benchmark.Results results;

    AnalysisPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        runButton.addActionListener(e -> runExperiment());
        csvButton.addActionListener(e -> exportCsv());
        pngButton.addActionListener(e -> saveCharts());
        csvButton.setEnabled(false);
        pngButton.setEnabled(false);
        kindBox.addActionListener(e -> updateCharts());
        progress.setStringPainted(true);
        progress.setPreferredSize(new Dimension(260, 22));

        JPanel north = new JPanel(new GridLayout(2, 1));
        north.add(UiUtil.row(runButton, progress, csvButton, pngButton));
        north.add(message);
        add(north, BorderLayout.NORTH);

        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        JPanel chartsPanel = new JPanel(new BorderLayout());
        chartsPanel.add(UiUtil.row(new JLabel("Input kind shown in the charts:"), kindBox), BorderLayout.NORTH);
        JPanel two = new JPanel(new GridLayout(1, 2, 6, 6));
        two.add(quadraticChart);
        two.add(linearithmicChart);
        chartsPanel.add(two, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tables", new JScrollPane(tablesPanel));
        tabs.addTab("Charts", chartsPanel);
        tabs.addTab("Theory and what to look for", new JScrollPane(theoryText()));
        add(tabs, BorderLayout.CENTER);

        UiUtil.show(message, "Press Run experiment. It times all six sorts for n = 100 to 10,000 on random, sorted and reverse sorted input "
                + "(3 runs averaged), then merge sort and quicksort for n = 50,000 and 100,000. It takes about 10 to 30 seconds.", UiUtil.INFO);
    }

    private JTextArea theoryText() {
        JTextArea t = UiUtil.textArea(20, 70);
        t.setText("Expected behaviour (theory)\n"
                + "- Bubble, selection and insertion sort are O(n^2) on average and in the worst case: doubling n should make the time about 4 times larger.\n"
                + "- Merge sort is O(n log n) for every input. Quicksort is O(n log n) on average.\n\n"
                + "Things the measurements should show\n"
                + "1. Insertion sort (and bubble sort with early exit) are very fast on already sorted input, because they make only n-1 comparisons and no swaps: O(n) best case.\n"
                + "2. Selection sort does the same number of comparisons on every input, so its time does not depend on the input order.\n"
                + "3. Quicksort with the first element as pivot slows down on sorted and reverse sorted input. The pivot is then the smallest (or largest) value, "
                + "so one side of every partition is empty, the recursion becomes n levels deep and the work is n + (n-1) + ... + 1 = O(n^2).\n"
                + "4. Choosing the pivot at random makes it very unlikely that every partition is lopsided, so the random-pivot version stays O(n log n) "
                + "on sorted and reverse sorted input.\n"
                + "5. For n = 100,000, merge sort and quicksort finish in milliseconds, while an O(n^2) sort would need billions of operations.\n\n"
                + "Compare your own numbers with these points in the report (Module D, part c).");
        t.setCaretPosition(0);
        return t;
    }


    private void runExperiment() {
        runButton.setEnabled(false);
        csvButton.setEnabled(false);
        pngButton.setEnabled(false);
        progress.setValue(0);
        UiUtil.show(message, "Running the experiment... please wait. The window stays usable.", UiUtil.INFO);

        new SwingWorker<Benchmark.Results, int[]>() {
            @Override
            protected Benchmark.Results doInBackground() {
                return Benchmark.run((done, total, msg) -> publish(new int[]{done, total}));
            }

            @Override
            protected void process(java.util.List<int[]> chunks) {
                int[] last = chunks.get(chunks.size() - 1);
                progress.setValue(100 * last[0] / last[1]);
            }

            @Override
            protected void done() {
                try {
                    results = get();
                    progress.setValue(100);
                    buildTables();
                    updateCharts();
                    csvButton.setEnabled(true);
                    pngButton.setEnabled(true);
                    UiUtil.show(message, "Experiment finished. See the Tables and Charts tabs. All times are averages of "
                            + Benchmark.REPEATS + " runs, in milliseconds.", UiUtil.OK);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    UiUtil.show(message, "The experiment failed: " + cause, UiUtil.ERROR);
                } finally {
                    runButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void buildTables() {
        tablesPanel.removeAll();
        for (int k = 0; k < InputGenerator.KINDS.length; k++) {
            String[] cols = new String[Benchmark.SIZES.length + 1];
            cols[0] = "Algorithm  (n = )";
            for (int s = 0; s < Benchmark.SIZES.length; s++) cols[s + 1] = String.valueOf(Benchmark.SIZES[s]);
            DefaultTableModel model = noEdit(cols);
            for (int a = 0; a < SortRegistry.NAMES.length; a++) {
                Object[] row = new Object[cols.length];
                row[0] = SortRegistry.NAMES[a];
                for (int s = 0; s < Benchmark.SIZES.length; s++) row[s + 1] = fmt(results.timeMs[k][a][s]);
                model.addRow(row);
            }
            addTable("Average time in ms: " + InputGenerator.KINDS[k] + " input", model);
        }
        String[] cols = new String[Benchmark.LARGE_SIZES.length + 1];
        cols[0] = "Algorithm  (n = )";
        for (int s = 0; s < Benchmark.LARGE_SIZES.length; s++) cols[s + 1] = String.valueOf(Benchmark.LARGE_SIZES[s]);
        DefaultTableModel large = noEdit(cols);
        for (int a = 0; a < Benchmark.LARGE_ALGORITHMS.length; a++) {
            Object[] row = new Object[cols.length];
            row[0] = Benchmark.LARGE_ALGORITHMS[a];
            for (int s = 0; s < Benchmark.LARGE_SIZES.length; s++) row[s + 1] = fmt(results.largeTimeMs[a][s]);
            large.addRow(row);
        }
        addTable("Average time in ms: larger random inputs", large);
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }

    private static DefaultTableModel noEdit(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void addTable(String title, DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(22);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(title));
        sp.setPreferredSize(new Dimension(700, 40 + 22 * (model.getRowCount() + 1)));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40 + 22 * (model.getRowCount() + 1)));
        tablesPanel.add(sp);
    }

    private static String fmt(double ms) {
        return ms < 1 ? String.format("%.4f", ms) : String.format("%.2f", ms);
    }


    private void updateCharts() {
        if (results == null) return;
        int k = kindBox.getSelectedIndex();
        String kind = InputGenerator.KINDS[k];
        quadraticChart.setData(QUADRATIC, xsFor(QUADRATIC, k), ysFor(QUADRATIC, k));
        linearithmicChart.setData(LINEARITHMIC, xsFor(LINEARITHMIC, k), ysFor(LINEARITHMIC, k));
        quadraticChart.setToolTipText(kind);
    }

    private double[][] xsFor(String[] algos, int kind) {
        double[][] xs = new double[algos.length][];
        for (int i = 0; i < algos.length; i++) xs[i] = pointsX(algos[i], kind);
        return xs;
    }

    private double[][] ysFor(String[] algos, int kind) {
        double[][] ys = new double[algos.length][];
        for (int i = 0; i < algos.length; i++) ys[i] = pointsY(algos[i], kind);
        return ys;
    }

    private int algoIndex(String name) {
        for (int i = 0; i < SortRegistry.NAMES.length; i++) if (SortRegistry.NAMES[i].equals(name)) return i;
        return -1;
    }

    private int largeIndex(String name) {
        for (int i = 0; i < Benchmark.LARGE_ALGORITHMS.length; i++) if (Benchmark.LARGE_ALGORITHMS[i].equals(name)) return i;
        return -1;
    }

    /** The larger sizes are only measured for random input and for the O(n log n) algorithms. */
    private boolean includeLarge(String name, int kind) {
        return kind == 0 && largeIndex(name) >= 0;
    }

    private double[] pointsX(String name, int kind) {
        int extra = includeLarge(name, kind) ? Benchmark.LARGE_SIZES.length : 0;
        double[] xs = new double[Benchmark.SIZES.length + extra];
        for (int s = 0; s < Benchmark.SIZES.length; s++) xs[s] = Benchmark.SIZES[s];
        for (int s = 0; s < extra; s++) xs[Benchmark.SIZES.length + s] = Benchmark.LARGE_SIZES[s];
        return xs;
    }

    private double[] pointsY(String name, int kind) {
        int a = algoIndex(name);
        int extra = includeLarge(name, kind) ? Benchmark.LARGE_SIZES.length : 0;
        double[] ys = new double[Benchmark.SIZES.length + extra];
        for (int s = 0; s < Benchmark.SIZES.length; s++) ys[s] = results.timeMs[kind][a][s];
        for (int s = 0; s < extra; s++) ys[Benchmark.SIZES.length + s] = results.largeTimeMs[largeIndex(name)][s];
        return ys;
    }


    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("sorting-experiment.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            Files.write(chooser.getSelectedFile().toPath(), Benchmark.toCsv(results).getBytes(StandardCharsets.UTF_8));
            UiUtil.show(message, "Saved " + chooser.getSelectedFile().getAbsolutePath(), UiUtil.OK);
        } catch (IOException ex) {
            UiUtil.show(message, "Could not save the file: " + ex.getMessage(), UiUtil.ERROR);
        }
    }

    private void saveCharts() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose a folder for the chart images");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String kind = ((String) kindBox.getSelectedItem()).toLowerCase().replace(' ', '-');
        try {
            File dir = chooser.getSelectedFile();
            ImageIO.write(quadraticChart.toImage(900, 520), "png", new File(dir, "chart-quadratic-sorts-" + kind + ".png"));
            ImageIO.write(linearithmicChart.toImage(900, 520), "png", new File(dir, "chart-nlogn-sorts-" + kind + ".png"));
            UiUtil.show(message, "Saved two chart images for \"" + kindBox.getSelectedItem() + "\" input in " + dir.getAbsolutePath()
                    + ". Change the input kind and save again for the other kinds.", UiUtil.OK);
        } catch (IOException ex) {
            UiUtil.show(message, "Could not save the images: " + ex.getMessage(), UiUtil.ERROR);
        }
    }
}
