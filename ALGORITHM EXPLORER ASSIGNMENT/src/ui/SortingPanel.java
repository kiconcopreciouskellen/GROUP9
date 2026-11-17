package ui;

import core.ArrayUtil;
import core.BigStack;
import core.Metrics;
import core.Timing;
import sorting.SortRegistry;
import sorting.SortTracer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Random;


 /**Module C: Sorting Lab. Run one algorithm and replay it step by step (Step, Back, Auto-play, speed),
 * see comparisons / moves / time, compare all algorithms on the same input, and read the complexity table.
 */
class SortingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    //Largest array whose steps are recorded and animated. */
    static final int ANIMATE_MAX = 40;
    static final int SORT_MAX = 100000;

    private final JTextField inputField = new JTextField("45, 12, 89, 23, 7, 64, 31", 30);
    private final JSpinner randomCount = new JSpinner(new SpinnerNumberModel(12, 1, SORT_MAX, 1));
    private final JComboBox<String> algorithmBox = new JComboBox<>(SortRegistry.NAMES);
    private final JButton runButton = UiUtil.button("Run sort");
    private final JButton backButton = UiUtil.button("\u25C0 Back");
    private final JButton stepButton = UiUtil.button("Step \u25B6");
    private final JToggleButton autoButton = new JToggleButton("Auto-play");
    private final JButton resetButton = UiUtil.button("Reset");
    private final JButton compareButton = UiUtil.button("Compare all");
    private final JSlider speedSlider = new JSlider(1, 10, 6);
    private final SortCanvas canvas = new SortCanvas();
    private final JTextArea log = UiUtil.textArea(10, 60);
    private final JLabel metrics = UiUtil.messageLabel();
    private final JLabel message = UiUtil.messageLabel();
    private final DefaultTableModel compareModel = readOnlyModel(
            new String[]{"Algorithm", "Comparisons", "Swaps / moves", "Time (avg ms)", "Sorted correctly"});
    private final JTabbedPane bottomTabs = new JTabbedPane();
    private final Timer autoTimer;

    private transient int[] generatedLarge;      // set when "Generate random" makes more values than fit in the text field
    private boolean settingText;                 //suppresses DocumentListener while we update inputField programmatically
    private transient SortTracer tracer;
    private int frameIndex;

    SortingPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton generate = UiUtil.button("Generate random");
        generate.addActionListener(e -> generateRandom());
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { edited(); }
            public void removeUpdate(DocumentEvent e) { edited(); }
            public void changedUpdate(DocumentEvent e) { edited(); }
        });
        runButton.addActionListener(e -> runSort());
        backButton.addActionListener(e -> go(frameIndex - 1));
        stepButton.addActionListener(e -> go(frameIndex + 1));
        resetButton.addActionListener(e -> go(0));
        autoButton.addActionListener(e -> toggleAuto());
        compareButton.addActionListener(e -> compareAll());
        speedSlider.setPreferredSize(new Dimension(110, 36));
        autoTimer = new Timer(500, e -> autoStep());
//Top controls:input row , algorithm action row and playback bar
        JPanel north = new JPanel(new GridLayout(3, 1));
        north.add(UiUtil.row(new JLabel("Numbers (comma-separated):"), inputField,
                new JLabel("or random n:"), randomCount, generate));
        north.add(UiUtil.row(new JLabel("Algorithm:"), algorithmBox, runButton, compareButton));
        north.add(UiUtil.row(backButton, stepButton, autoButton, resetButton,
                new JLabel("  Speed (slow-fast):"), speedSlider));
        add(north, BorderLayout.NORTH);

        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(log);
        bottomTabs.addTab("Array after every pass / step", logScroll);
        JTable compareTable = new JTable(compareModel);
        compareTable.setRowHeight(22);
        bottomTabs.addTab("Compare all", new JScrollPane(compareTable));
        bottomTabs.addTab("Complexity help", new JScrollPane(complexityTable()));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, canvas, bottomTabs);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(metrics);
        south.add(message);
        add(south, BorderLayout.SOUTH);

        UiUtil.show(metrics, "Comparisons: -     Swaps / moves: -     Time: -", UiUtil.INFO);
        UiUtil.show(message, "Steps are shown for up to " + ANIMATE_MAX + " numbers. Larger inputs run without the step view.", UiUtil.INFO);
        updateButtons(false);
    }
//Creates a tabel model where users can select cells but cannot edit values
    private static DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable complexityTable() {
        DefaultTableModel model = readOnlyModel(new String[]{"Algorithm", "Best case", "Average case", "Worst case", "Extra space", "Stable"});
        for (String name : SortRegistry.NAMES) {
            String[] f = SortRegistry.facts(name);
            model.addRow(new Object[]{name, f[0], f[1], f[2], f[3], f[4]});
        }
        JTable table = new JTable(model);
        table.setRowHeight(22);
        return table;
    }

    // input

    private void edited() {
        if (!settingText) generatedLarge = null;
        invalidateRun();
    }

    //The input changed, so the step view of the previous run no longer matches it.
    private void invalidateRun() {
        stopAuto();
        tracer = null;
        canvas.show(null, "The input changed. Press \"Run sort\" to load it.");
        updateButtons(false);
    }

    private void generateRandom() {
        int n = (Integer) randomCount.getValue();
        Random rnd = new Random();
        int[] values = new int[n];
        //keeps values small when animating so bar labels stay easy to read=
        int bound = n <= ANIMATE_MAX ? 100 : 1_000_000;
        for (int i = 0; i < n; i++) values[i] = rnd.nextInt(bound);
        settingText = true;
        if (n <= 200) {
            generatedLarge = null;
            inputField.setText(joined(values));
        } else {
            //Avoid Freezing swing by stuffing thousands of numbers into a JTextField
            generatedLarge = values;
            inputField.setText("(random array of " + n + " values)");
        }
        settingText = false;
        invalidateRun();
        UiUtil.show(message, "Generated " + n + " random numbers.", UiUtil.INFO);
    }

    private static String joined(int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i]);
        }
        return sb.toString();
    }

    //The numbers to sort, or an IllegalArgumentException with a readable message.
    private int[] readInput() {
        if (generatedLarge != null) return generatedLarge.clone();
        return ArrayUtil.parse(inputField.getText(), SORT_MAX);
    }

    //  run one algorithm -

    private void runSort() {
        stopAuto();
        final int[] input;
        try {
            input = readInput();
        } catch (IllegalArgumentException ex) {
            UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
            return;
        }
        final String name = (String) algorithmBox.getSelectedItem();
        final boolean animate = input.length <= ANIMATE_MAX;
        setBusy(true);
        UiUtil.show(message, "Running " + name + " on " + input.length + " numbers...", UiUtil.INFO);
//Runs off the the EDT so that large sorts(O(n^2) don't freeze th window
        //Big Stack avoids StackOverflow on worst-case quicksort
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                final Object[] out = new Object[4];
                BigStack.run(() -> {
                    // 1. traced run (steps), small inputs only
                    SortTracer t = animate ? new SortTracer(true) : SortTracer.OFF;
                    Metrics m = new Metrics();
                    int[] work = input.clone();
                    SortRegistry.sort(name, work, m, t);
                    // 2. timed runs without tracing
                    int repeats = input.length <= 5000 ? 3 : 1;
                    double ms = timeSort(name, input, repeats);
                    out[0] = t;
                    out[1] = m;
                    out[2] = ms;
                    out[3] = ArrayUtil.isSorted(work);
                });
                return out;
            }

            @Override
            protected void done() {
                try {
                    Object[] r = get();
                    finishRun(name, input, (SortTracer) r[0], (Metrics) r[1], (Double) r[2], (Boolean) r[3], animate);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    UiUtil.show(message, "Could not run the sort: " + cause, UiUtil.ERROR);
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }

    // Average time of repeated silent runs. A warm-up run comes first for small inputs.
    static double timeSort(String name, int[] input, int repeats) {
        if (input.length <= 5000) SortRegistry.sort(name, input.clone(), new Metrics(), SortTracer.OFF);
        long total = 0;
        for (int i = 0; i < repeats; i++) {
            int[] copy = input.clone();
            long start = System.nanoTime();
            SortRegistry.sort(name, copy, new Metrics(), SortTracer.OFF);
            total += System.nanoTime() - start;
        }
        return total / (double) repeats / 1_000_000.0;
    }

    private void finishRun(String name, int[] input, SortTracer t, Metrics m, double ms, boolean sorted, boolean animate) {
        tracer = animate ? t : null;
        String meaning = SortRegistry.moveMeaning(name);
        UiUtil.show(metrics, "Comparisons: " + m.getComparisons() + "     Swaps / moves (" + meaning + "): " + m.getMoves()
                + "     Time: " + Timing.format(ms) + " (average, steps not drawn while timing)", UiUtil.INFO);

        StringBuilder sb = new StringBuilder();
        if (animate) {
            sb.append("Initial array: ").append(ArrayUtil.format(input)).append("\n\n");
            int pass = 0;
            for (int i = 0; i < t.size(); i++) {
                SortTracer.Frame f = t.get(i);
                if (f.passEnd) {
                    pass++;
                    sb.append(pass).append(". ").append(f.note).append('\n').append("   ").append(ArrayUtil.format(f.values)).append('\n');
                }
            }
            log.setText(sb.toString());
            log.setCaretPosition(0);
            frameIndex = 0;
            go(0);
            UiUtil.show(message, name + " finished " + (sorted ? "and the array is sorted." : "but the result is NOT sorted!")
                    + " Use Step / Back / Auto-play to replay it (" + t.size() + " frames).", sorted ? UiUtil.OK : UiUtil.ERROR);
        } else {
            sb.append("Input has ").append(input.length).append(" numbers, which is more than ").append(ANIMATE_MAX)
              .append(", so the step view is switched off.\nThe counters and the time above are for the full run.\n");
            log.setText(sb.toString());
            canvas.show(null, "No step view for " + input.length + " numbers.");
            UiUtil.show(message, name + " finished " + (sorted ? "and the array is sorted." : "but the result is NOT sorted!"),
                    sorted ? UiUtil.OK : UiUtil.ERROR);
        }
        updateButtons(animate);
    }

    // replay

    private void go(int index) {
        if (tracer == null || tracer.size() == 0) return;
        frameIndex = Math.max(0, Math.min(index, tracer.size() - 1));
        SortTracer.Frame f = tracer.get(frameIndex);
        canvas.show(f, "Frame " + (frameIndex + 1) + " of " + tracer.size() + ":  " + f.note);
        if (frameIndex == tracer.size() - 1) stopAuto();
        updateButtons(true);
    }

    private void toggleAuto() {
        if (autoButton.isSelected()) {
            if (tracer == null) {
                autoButton.setSelected(false);
                return;
            }
            if (frameIndex >= tracer.size() - 1) go(0);
            //Slider goes 1--10  mapping to 1000ms-- 100ms delay per frame
            autoTimer.setDelay(1100 - speedSlider.getValue() * 100);
            autoTimer.start();
        } else {
            autoTimer.stop();
        }
    }

    private void autoStep() {
        //RE-reads the slider  each tick so speed changes apply immediately even mid-playback
        autoTimer.setDelay(1100 - speedSlider.getValue() * 100);
        go(frameIndex + 1);
    }

    private void stopAuto() {
        autoTimer.stop();
        autoButton.setSelected(false);
    }

    private void updateButtons(boolean haveFrames) {
        boolean have = haveFrames && tracer != null;
        backButton.setEnabled(have && frameIndex > 0);
        stepButton.setEnabled(have && frameIndex < tracer.size() - 1);
        autoButton.setEnabled(have);
        resetButton.setEnabled(have);
    }

    private void setBusy(boolean busy) {
        runButton.setEnabled(!busy);
        compareButton.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    // ----- compare all -----

    private void compareAll() {
        stopAuto();
        final int[] input;
        try {
            input = readInput();
        } catch (IllegalArgumentException ex) {
            UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
            return;
        }
        setBusy(true);
        UiUtil.show(message, "Running all algorithms on " + input.length + " numbers"
                + (input.length > 10000 ? " (the O(n^2) sorts can take many seconds)..." : "..."), UiUtil.INFO);

        new SwingWorker<Object[][], Void>() {
            @Override
            protected Object[][] doInBackground() {
                final Object[][] rows = new Object[SortRegistry.NAMES.length][];
                BigStack.run(() -> {
                    int repeats = input.length <= 5000 ? 3 : 1;
                    for (int i = 0; i < SortRegistry.NAMES.length; i++) {
                        String name = SortRegistry.NAMES[i];
                        Metrics m = new Metrics();
                        int[] work = input.clone();
                        SortRegistry.sort(name, work, m, SortTracer.OFF);
                        double ms = timeSort(name, input, repeats);
                        rows[i] = new Object[]{name, m.getComparisons(), m.getMoves(), String.format("%.4f", ms),
                                ArrayUtil.isSorted(work) ? "yes" : "NO"};
                    }
                });
                return rows;
            }

            @Override
            protected void done() {
                try {
                    Object[][] rows = get();
                    compareModel.setRowCount(0);
                    for (Object[] row : rows) compareModel.addRow(row);
                    bottomTabs.setSelectedIndex(1);
                    UiUtil.show(message, "Compared all algorithms on the same " + input.length + " numbers. "
                            + "Swaps / moves = swaps (bubble, selection, insertion, quicksort) or array writes (merge sort). "
                            + "The random-pivot counts change from run to run.", UiUtil.OK);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    UiUtil.show(message, "Could not compare: " + cause, UiUtil.ERROR);
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }
}
