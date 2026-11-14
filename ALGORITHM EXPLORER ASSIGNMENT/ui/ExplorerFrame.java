package ui;

import core.AlgorithmNames;
import java.awt.*;
import javax.swing.*;

/**
 * The window: controls at the top, step-by-step log in the centre, metrics at the bottom.
 * It contains no algorithm code. Another class registers a handler with onRun() and calls showResult().
 */
public class ExplorerFrame extends JFrame {
    private static final long serialVersionUID = 1L;


    private static final String DEFAULT_ARRAY = "45, 12, 89, 23, 7, 64, 31";

    private final JComboBox<String> categoryCombo = new JComboBox<>(AlgorithmNames.CATEGORIES);
    private final JComboBox<String> algorithmCombo = new JComboBox<>();
    private final JTextField inputField = new JTextField();
    private final JTextField targetField = new JTextField();
    private final JLabel inputLabel = new JLabel();
    private final JLabel targetLabel = new JLabel();
    private final JTextArea outputArea = new JTextArea();
    private final JLabel opsLabel = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final JLabel complexityLabel = new JLabel();
    private final JButton runButton = new JButton("Run / Execute");
    private final JButton clearButton = new JButton("Clear Output");

    public ExplorerFrame() {
        setTitle("CSC2107 - Algorithm Explorer");
        setSize(950, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Controls
        JPanel topPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        topPanel.setBorder(BorderFactory.createTitledBorder("Algorithm Controls"));
        topPanel.add(new JLabel("  Select Category:"));
        topPanel.add(categoryCombo);
        topPanel.add(new JLabel("  Select Algorithm / Structure:"));
        topPanel.add(algorithmCombo);
        topPanel.add(inputLabel);
        topPanel.add(inputField);
        topPanel.add(targetLabel);
        topPanel.add(targetField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Output
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Step-by-Step Execution Log"));
        add(scrollPane, BorderLayout.CENTER);

        // Metrics
        JPanel metricsPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Performance Metrics and Complexity"));
        Font bold = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        opsLabel.setFont(bold);
        timeLabel.setFont(bold);
        complexityLabel.setFont(bold);
        metricsPanel.add(opsLabel);
        metricsPanel.add(timeLabel);
        metricsPanel.add(complexityLabel);
        add(metricsPanel, BorderLayout.SOUTH);

        resetMetrics();
        reloadAlgorithms();
        categoryCombo.addActionListener(e -> reloadAlgorithms());
        algorithmCombo.addActionListener(e -> applyHints());
        applyHints();
        clearButton.addActionListener(e -> clearOutput());
    }

    // what the controller needs

    public String getCategory() {
        return (String) categoryCombo.getSelectedItem();
    }

    public String getAlgorithm() {
        return (String) algorithmCombo.getSelectedItem();
    }

    public String getInputText() {
        return inputField.getText().trim();
    }

    public String getTargetText() {
        return targetField.getText().trim();
    }

    public void onRun(Runnable handler) {
        runButton.addActionListener(e -> handler.run());
    }

    public void setBusy(boolean busy) {
        runButton.setEnabled(!busy);
        categoryCombo.setEnabled(!busy);
        algorithmCombo.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        if (busy) outputArea.setText("Running... please wait.");
    }

    public void showResult(String log, String operations, String time, String complexity) {
        outputArea.setText(log);
        outputArea.setCaretPosition(0);
        opsLabel.setText("Operations Count: " + operations);
        timeLabel.setText("Execution Time (algorithm only, logging excluded): " + time);
        complexityLabel.setText("Complexity: " + complexity);
    }

    public void showError(String message) {
        outputArea.setText("ERROR: " + message);
        resetMetrics();
    }

    // internal stuff

    private void clearOutput() {
        outputArea.setText("");
        resetMetrics();
    }

    private void resetMetrics() {
        opsLabel.setText("Operations Count: 0");
        timeLabel.setText("Execution Time (algorithm only, logging excluded): 0.0000 ms");
        complexityLabel.setText("Complexity: select an algorithm and press Run");
    }

    private void reloadAlgorithms() {
        algorithmCombo.removeAllItems();
        for (String name : AlgorithmNames.algorithmsFor(getCategory())) {
            algorithmCombo.addItem(name);
        }
    }

    /** Updates the field labels, default values and enabled state to match the chosen algorithm. */
    private void applyHints() {
        String algorithm = getAlgorithm();
        if (algorithm == null) return;

        boolean useInput = true;
        boolean useTarget = false;
        String inputText = "  Input array (comma-separated integers):";
        String targetText = "  Target value (not used):";
        String inputDefault = DEFAULT_ARRAY;
        String targetDefault = "";

        switch (algorithm) {
            case AlgorithmNames.BINARY_SEARCH:
            case AlgorithmNames.LINEAR_SEARCH:
                useTarget = true;
                targetText = "  Target value to search for:";
                targetDefault = "23";
                break;
            case AlgorithmNames.FACTORIAL:
                inputText = "  n (0 to 20):";
                inputDefault = "5";
                break;
            case AlgorithmNames.FIB_NAIVE:
                inputText = "  n (0 to 35):";
                inputDefault = "6";
                break;
            case AlgorithmNames.FIB_MEMO:
                inputText = "  n (0 to 90):";
                inputDefault = "50";
                break;
            case AlgorithmNames.HANOI:
                inputText = "  Number of disks (1 to 20):";
                inputDefault = "3";
                break;
            case AlgorithmNames.STACK:
                inputText = "  Values to push, in order:";
                inputDefault = "10, 20, 30";
                useTarget = true;
                targetText = "  Extra value to push (optional):";
                targetDefault = "40";
                break;
            case AlgorithmNames.QUEUE:
                inputText = "  Values to enqueue, in order:";
                inputDefault = "10, 20, 30";
                useTarget = true;
                targetText = "  Extra value to enqueue (optional):";
                targetDefault = "40";
                break;
            case AlgorithmNames.LINKED_LIST:
                inputText = "  Values to insert at the end, in order:";
                inputDefault = "5, 3, 8, 1";
                useTarget = true;
                targetText = "  Value to search for, then delete (optional):";
                targetDefault = "3";
                break;
            case AlgorithmNames.BST:
                inputText = "  Values to insert, in order:";
                inputDefault = "50, 30, 70, 20, 40, 60, 80";
                useTarget = true;
                targetText = "  Value to search for, then delete (optional):";
                targetDefault = "40";
                break;
            case AlgorithmNames.BENCHMARK:
                useInput = false;
                inputText = "  Input (not used: the benchmark generates its own data):";
                inputDefault = "";
                break;
            default:
                break; // sorting algorithms use the defaults
        }

        inputLabel.setText(inputText);
        targetLabel.setText(targetText);
        inputField.setText(inputDefault);
        targetField.setText(targetDefault);
        inputField.setEnabled(useInput);
        targetField.setEnabled(useTarget);
    }
}
