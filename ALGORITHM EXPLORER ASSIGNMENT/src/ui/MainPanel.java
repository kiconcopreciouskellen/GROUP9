package ui;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public MainPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 13f));
        tabs.addTab("A: Recursion Lab", new RecursionTab());
        tabs.addTab("B: Data Structures Lab", new StructuresTab());
        tabs.addTab("C: Sorting Lab", new SortingPanel());
        tabs.addTab("D: Time Complexity Analysis", new AnalysisPanel());
        add(tabs, BorderLayout.CENTER);
    }
}
