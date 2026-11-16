package ui;

import javax.swing.*;
import java.awt.*;

/** Small shared helpers for building the screens. */
final class UiUtil {
    static final Color INFO = new Color(30, 90, 160);
    static final Color ERROR = new Color(180, 30, 30);
    static final Color OK = new Color(20, 120, 50);

    private UiUtil() { }

    static JLabel messageLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return label;
    }

    static void show(JLabel label, String text, Color color) {
        label.setForeground(color);
        label.setText("<html><body style='width:700px'>" + text.replace("&", "&amp;").replace("<", "&lt;") + "</body></html>");
    }

    static JTextArea textArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    static JPanel row(Component... parts) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        for (Component c : parts) p.add(c);
        return p;
    }

    static JButton button(String text) {
        JButton b = new JButton(text);
        b.setMargin(new Insets(3, 8, 3, 8));
        return b;
    }

    static void enableAll(Container c, boolean enabled) {
        for (Component child : c.getComponents()) {
            child.setEnabled(enabled);
            if (child instanceof Container) enableAll((Container) child, enabled);
        }
    }
}
