package ui;

import javax.swing.*;

//Data Structures Lab
class StructuresTab extends JTabbedPane {
    private static final long serialVersionUID = 1L;

    StructuresTab() {
        addTab("Stack", new StackPanel());
        addTab("Linear Queue", new QueuePanel(false));
        addTab("Circular Queue", new QueuePanel(true));
        addTab("Singly Linked List", new LinkedListPanel(false));
        addTab("Doubly Linked List", new LinkedListPanel(true));
        addTab("Bracket Checker (stack application)", new BracketPanel());
    }
}


