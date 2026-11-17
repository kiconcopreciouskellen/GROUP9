package ui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            JFrame frame = new JFrame("Group 9 - Algorithm Explorer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MainPanel());
            frame.setSize(1100, 760);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
