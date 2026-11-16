package ui;

import core.ArrayUtil;
import core.Metrics;
import core.Timing;
import recursion.FactorialAlgorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**recursive and iterative factorial. */
class FactorialPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField nField = new JTextField("5", 6);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Method", "Result", "Calls / iterations", "Time (average of 5 runs)", "Time / space"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel message = UiUtil.messageLabel();

    FactorialPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton run = UiUtil.button("Compute both");
        run.addActionListener(e -> compute());
        nField.addActionListener(e -> compute());
        add(UiUtil.row(new JLabel("n (0 to " + FactorialAlgorithms.MAX_N + "):"), nField, run), BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(24);
        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(message, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JTextArea note = UiUtil.textArea(5, 60);
        note.setText("0! is defined as 1, and the program handles n = 0. Negative numbers are rejected. "
                + "n is limited to " + FactorialAlgorithms.MAX_N + " because 21! is larger than the biggest 64-bit integer.\n"
                + "The recursive version makes n calls (n = 0 and n = 1 make one call each) and uses O(n) stack space. "
                + "The iterative version runs a loop of n-1 iterations and uses O(1) space. Both take O(n) time.");
        JScrollPane scroll = new JScrollPane(note);
        scroll.setBorder(BorderFactory.createTitledBorder("Notes"));
        add(scroll, BorderLayout.SOUTH);
    }

    private void compute() {
        model.setRowCount(0);
        int n;
        try {
            n = ArrayUtil.parseInt(nField.getText(), "n", Integer.MIN_VALUE, Integer.MAX_VALUE);
            FactorialAlgorithms.validate(n);
        } catch (IllegalArgumentException ex) {
            UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
            return;
        }

        Metrics m1 = new Metrics();
        long r1 = FactorialAlgorithms.recursive(n, m1);
        double t1 = Timing.averageMillis(5, () -> FactorialAlgorithms.recursive(n, new Metrics()));
        model.addRow(new Object[]{"Recursive", r1, m1.getCalls(), Timing.format(t1), "O(n) time, O(n) stack"});

        Metrics m2 = new Metrics();
        long r2 = FactorialAlgorithms.iterative(n, m2);
        double t2 = Timing.averageMillis(5, () -> FactorialAlgorithms.iterative(n, new Metrics()));
        model.addRow(new Object[]{"Iterative", r2, m2.getCalls(), Timing.format(t2), "O(n) time, O(1) space"});

        UiUtil.show(message, n + "! = " + r1, UiUtil.OK);
    }
}
