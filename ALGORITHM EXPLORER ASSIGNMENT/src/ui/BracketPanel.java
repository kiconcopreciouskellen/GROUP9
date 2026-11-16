package ui;

import structures.BracketChecker;

import javax.swing.*;
import java.awt.*;

class BracketPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField expressionField = new JTextField("{ ( [ ] ) }", 30);
    private final JTextArea steps = UiUtil.textArea(14, 70);
    private final JLabel message = UiUtil.messageLabel();

    BracketPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton check = UiUtil.button("Check balance");
        check.addActionListener(e -> check());
        expressionField.addActionListener(e -> check());
        JButton ok = UiUtil.button("Example: balanced");
        ok.addActionListener(e -> {
            expressionField.setText("{ ( [ ] ) } [ ( ) ]");
            check();
        });
        JButton bad = UiUtil.button("Example: not balanced");
        bad.addActionListener(e -> {
            expressionField.setText("{ ( [ ) ] }");
            check();
        });
        add(UiUtil.row(new JLabel("Expression:"), expressionField, check, ok, bad), BorderLayout.NORTH);

        steps.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(steps);
        scroll.setBorder(BorderFactory.createTitledBorder("Stack after each symbol"));
        add(scroll, BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        UiUtil.show(message, "Type an expression using ( ) [ ] { } and press Check. Other characters are ignored.", UiUtil.INFO);
    }

    private void check() {
        String expression = expressionField.getText();
        if (expression.trim().isEmpty()) {
            steps.setText("");
            UiUtil.show(message, "The expression is empty. Type some brackets, for example { ( [ ] ) }.", UiUtil.ERROR);
            return;
        }
        StringBuilder log = new StringBuilder();
        boolean balanced = BracketChecker.check(expression, log);
        steps.setText(log.toString());
        steps.setCaretPosition(0);
        UiUtil.show(message, balanced ? "The expression is balanced." : "The expression is NOT balanced.",
                balanced ? UiUtil.OK : UiUtil.ERROR);
    }
}
