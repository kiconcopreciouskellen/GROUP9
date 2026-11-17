package ui;

import javax.swing.*;

// Module A: Recursion Lab (Fibonacci, Factorial, Tower of Hanoi).
class RecursionTab extends JTabbedPane {
    private static final long serialVersionUID = 1L;

    RecursionTab() {
        //Each tab is self-contained with its own input  controls and trace/animation view
        addTab("Fibonacci", new FibonacciPanel());
        addTab("Factorial", new FactorialPanel());
        addTab("Tower of Hanoi", new HanoiPanel());
    }
}
