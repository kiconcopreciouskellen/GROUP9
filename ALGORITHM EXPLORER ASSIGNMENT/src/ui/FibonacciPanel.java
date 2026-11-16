package ui;

import core.ArrayUtil;
import core.Metrics;
import core.Timing;
import recursion.FibonacciAlgorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**Fibonacci by naive recursion, memoized recursion and bottom-up dynamic programming. */
class FibonacciPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField nField = new JTextField("20", 6);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Method", "Result", "Calls / iterations", "Time (average of 5 runs)", "Time complexity"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel message = UiUtil.messageLabel();

    FibonacciPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton run = UiUtil.button("Compute all three");
        run.addActionListener(e -> compute());
        nField.addActionListener(e -> compute());
        add(UiUtil.row(new JLabel("n (0 to " + FibonacciAlgorithms.MAX_FAST + "):"), nField, run), BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(24);
        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(message, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JTextArea why = UiUtil.textArea(7, 60);
        why.setText("Why is naive recursion limited to n <= " + FibonacciAlgorithms.MAX_NAIVE + "?\n"
                + "fib(n) calls fib(n-1) and fib(n-2), and both of them recompute the same smaller values again and again. "
                + "The number of calls grows by about 1.6 times for every increase of n by 1, which is O(2^n). "
                + "fib(30) already needs 2,692,537 calls, fib(40) would need over 331 million, and fib(50) over 40 billion, "
                + "so the screen would freeze.\n"
                + "Memoized recursion stores each fib(k) the first time it is computed, so it makes only about 2n-1 calls (O(n)). "
                + "Bottom-up dynamic programming uses a loop from fib(0) and fib(1) upwards: n-1 iterations (O(n)), O(1) extra space.\n"
                + "The largest n allowed for the fast methods is " + FibonacciAlgorithms.MAX_FAST
                + ", because fib(93) does not fit in a 64-bit integer.");
        JScrollPane whyScroll = new JScrollPane(why);
        whyScroll.setBorder(BorderFactory.createTitledBorder("Explanation"));
        add(whyScroll, BorderLayout.SOUTH);
    }

    private void compute() {
        model.setRowCount(0);
        int n;
        try {
            n = ArrayUtil.parseInt(nField.getText(), "n", 0, FibonacciAlgorithms.MAX_FAST);
        } catch (IllegalArgumentException ex) {
            UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
            return;
        }

        if (n <= FibonacciAlgorithms.MAX_NAIVE) {
            Metrics m = new Metrics();
            long value = FibonacciAlgorithms.naive(n, m);
            double ms = Timing.averageMillis(5, () -> FibonacciAlgorithms.naive(n, new Metrics()));
            model.addRow(new Object[]{"Naive recursion", value, m.getCalls(), Timing.format(ms), "O(2^n)"});
        } else {
            model.addRow(new Object[]{"Naive recursion", "skipped", "-", "-",
                    "n > " + FibonacciAlgorithms.MAX_NAIVE + ": too many calls (see explanation)"});
        }

        Metrics m2 = new Metrics();
        long memo = FibonacciAlgorithms.memoized(n, m2);
        double t2 = Timing.averageMillis(5, () -> FibonacciAlgorithms.memoized(n, new Metrics()));
        model.addRow(new Object[]{"Memoized recursion", memo, m2.getCalls(), Timing.format(t2), "O(n)"});

        Metrics m3 = new Metrics();
        long dp = FibonacciAlgorithms.bottomUp(n, m3);
        double t3 = Timing.averageMillis(5, () -> FibonacciAlgorithms.bottomUp(n, new Metrics()));
        model.addRow(new Object[]{"Bottom-up dynamic programming", dp, m3.getCalls(), Timing.format(t3), "O(n)"});

        UiUtil.show(message, "fib(" + n + ") = " + dp, UiUtil.OK);
    }
}
