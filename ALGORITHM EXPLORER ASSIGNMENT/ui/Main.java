package ui;

import javax.swing.SwingUtilities;

/** Entry point: builds the window and attaches the controller. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExplorerFrame frame = new ExplorerFrame();
            new AlgorithmRunner(frame);
            frame.setVisible(true);
        });
    }
}
