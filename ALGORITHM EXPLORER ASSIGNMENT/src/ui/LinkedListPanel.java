package ui;

import core.ArrayUtil;
import java.awt.*;
import javax.swing.*;
import structures.DoublyLinkedList;
import structures.SinglyLinkedList;

//Singly or doubly linked list with every insertion, deletion and display operation
class LinkedListPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    //Common view of the two list classes, so one screen can drive either. 
    private interface Model {
        void insertAtBeginning(int v);
        void insertAtEnd(int v);
        void insertBefore(int target, int v);
        void insertAfter(int target, int v);
        int deleteAtBeginning();
        int deleteAtEnd();
        int deleteBefore(int target);
        int deleteAfter(int target);
        void delete(int v);
        int search(int v);
        int size();
        int[] toArray();
        String forward();
        String backward();
    }

    private static Model singly() {
        final SinglyLinkedList l = new SinglyLinkedList();
        return new Model() {
            public void insertAtBeginning(int v) { l.insertAtBeginning(v); }
            public void insertAtEnd(int v) { l.insertAtEnd(v); }
            public void insertBefore(int t, int v) { l.insertBefore(t, v); }
            public void insertAfter(int t, int v) { l.insertAfter(t, v); }
            public int deleteAtBeginning() { return l.deleteAtBeginning(); }
            public int deleteAtEnd() { return l.deleteAtEnd(); }
            public int deleteBefore(int t) { return l.deleteBefore(t); }
            public int deleteAfter(int t) { return l.deleteAfter(t); }
            public void delete(int v) { l.delete(v); }
            public int search(int v) { return l.search(v); }
            public int size() { return l.size(); }
            public int[] toArray() { return l.toArray(); }
            public String forward() { return l.displayForward(); }
            public String backward() { return ""; }
        };
    }

    private static Model doubly() {
        final DoublyLinkedList l = new DoublyLinkedList();
        return new Model() {
            public void insertAtBeginning(int v) { l.insertAtBeginning(v); }
            public void insertAtEnd(int v) { l.insertAtEnd(v); }
            public void insertBefore(int t, int v) { l.insertBefore(t, v); }
            public void insertAfter(int t, int v) { l.insertAfter(t, v); }
            public int deleteAtBeginning() { return l.deleteAtBeginning(); }
            public int deleteAtEnd() { return l.deleteAtEnd(); }
            public int deleteBefore(int t) { return l.deleteBefore(t); }
            public int deleteAfter(int t) { return l.deleteAfter(t); }
            public void delete(int v) { l.delete(v); }
            public int search(int v) { return l.search(v); }
            public int size() { return l.size(); }
            public int[] toArray() { return l.toArray(); }
            public String forward() { return l.displayForward(); }
            public String backward() { return l.displayBackward(); }
        };
    }

    private final boolean isDoubly;
    private transient Model list;
    private final JTextField valueField = new JTextField("10", 6);
    private final JTextField refField = new JTextField("10", 6);
    private final JLabel message = UiUtil.messageLabel();
    private final Canvas canvas = new Canvas();
    private int highlight = -1;

    LinkedListPanel(boolean doubly) {
        this.isDoubly = doubly;
        this.list = doubly ? doubly() : singly();
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel inputs = UiUtil.row(new JLabel("Value:"), valueField,
                new JLabel("   Reference element (for before / after):"), refField);

        JPanel insert = UiUtil.row(new JLabel("Insert:"),
                op("At beginning", () -> {
                    int v = value();
                    list.insertAtBeginning(v);
                    highlight = 0;
                    return "Inserted " + v + " at the beginning";
                }),
                op("At end", () -> {
                    int v = value();
                    list.insertAtEnd(v);
                    highlight = list.size() - 1;
                    return "Inserted " + v + " at the end";
                }),
                op("Before element", () -> {
                    int v = value();
                    int ref = reference();
                    int idx = list.search(ref);
                    list.insertBefore(ref, v);
                    highlight = Math.max(idx, 0);
                    return "Inserted " + v + " before " + ref;
                }),
                op("After element", () -> {
                    int v = value();
                    int ref = reference();
                    int idx = list.search(ref);
                    list.insertAfter(ref, v);
                    highlight = idx + 1;
                    return "Inserted " + v + " after " + ref;
                }));

        JPanel delete = UiUtil.row(new JLabel("Delete:"),
                op("At beginning", () -> "Deleted " + list.deleteAtBeginning() + " from the beginning"),
                op("At end", () -> "Deleted " + list.deleteAtEnd() + " from the end"),
                op("Before element", () -> {
                    int ref = reference();
                    return "Deleted " + list.deleteBefore(ref) + ", the element before " + ref;
                }),
                op("After element", () -> {
                    int ref = reference();
                    return "Deleted " + list.deleteAfter(ref) + ", the element after " + ref;
                }),
                op("Given element (Value)", () -> {
                    int v = value();
                    list.delete(v);
                    return "Deleted element " + v;
                }));

        JPanel query = UiUtil.row(new JLabel("Other:"),
                op("Search (Value)", () -> {
                    int v = value();
                    int idx = list.search(v);
                    if (idx < 0) throw new IllegalArgumentException("Element " + v + " was not found in the list.");
                    highlight = idx;
                    return "Found " + v + " at position " + idx + " (counting from 0)";
                }),
                op("Size", () -> "Size = " + list.size()),
                op("Display forward", () -> "Forward: " + list.forward()));
        if (doubly) query.add(op("Display backward", () -> "Backward: " + list.backward()));
        JButton clear = UiUtil.button("Clear list");
        clear.addActionListener(e -> {
            list = isDoubly ? doubly() : singly();
            highlight = -1;
            UiUtil.show(message, "The list was cleared.", UiUtil.INFO);
            canvas.revalidate();
            canvas.repaint();
        });
        query.add(clear);

        JPanel north = new JPanel(new GridLayout(4, 1));
        north.add(inputs);
        north.add(insert);
        north.add(delete);
        north.add(query);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        UiUtil.show(message, "Empty " + (doubly ? "doubly" : "singly") + " linked list. Type a value and press an Insert button.", UiUtil.INFO);
    }

    private interface Action {
        String perform();
    }

    private JButton op(String label, Action action) {
        JButton b = UiUtil.button(label);
        b.addActionListener(e -> {
            highlight = -1;
            try {
                UiUtil.show(message, action.perform(), UiUtil.OK);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
            }
            canvas.revalidate();
            canvas.repaint();
        });
        return b;
    }

    private int value() {
        return ArrayUtil.parseInt(valueField.getText(), "Value", Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private int reference() {
        return ArrayUtil.parseInt(refField.getText(), "Reference element", Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    //Draws the nodes as boxes with arrows
    private class Canvas extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final int NODE_W = 76, NODE_H = 44, GAP = 52, MARGIN = 40;

        Canvas() {
            setBackground(Color.WHITE);
        }

        @Override
        public Dimension getPreferredSize() {
            int n = list.size();
            int w = MARGIN * 2 + (n + 1) * (NODE_W + GAP) + 60;
            return new Dimension(Math.max(700, w), 200);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            int[] values = list.toArray();
            int y = getHeight() / 2 - NODE_H / 2;

            if (values.length == 0) {
                g.setColor(Color.GRAY);
                g.drawString("HEAD = null  (the list is empty)", MARGIN, y + NODE_H / 2);
                return;
            }
            for (int i = 0; i < values.length; i++) {
                int x = MARGIN + i * (NODE_W + GAP);
                boolean hl = i == highlight;
                g.setColor(hl ? new Color(255, 235, 120) : new Color(190, 220, 250));
                g.fillRect(x, y, NODE_W, NODE_H);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, NODE_W, NODE_H);
                if (isDoubly) {
                    g.drawLine(x + 18, y, x + 18, y + NODE_H);
                    g.drawLine(x + NODE_W - 18, y, x + NODE_W - 18, y + NODE_H);
                    center(g, String.valueOf(values[i]), x + 18, x + NODE_W - 18, y + NODE_H / 2 + 5);
                } else {
                    g.drawLine(x + NODE_W - 24, y, x + NODE_W - 24, y + NODE_H);
                    center(g, String.valueOf(values[i]), x, x + NODE_W - 24, y + NODE_H / 2 + 5);
                }
                g.setColor(Color.GRAY);
                g.setFont(getFont().deriveFont(Font.PLAIN, 11f));
                center(g, "pos " + i, x, x + NODE_W, y + NODE_H + 16);
                g.setFont(getFont().deriveFont(Font.BOLD, 14f));

                //arrows to the next node
                if (i < values.length - 1) {
                    int x1 = x + NODE_W;
                    int x2 = x + NODE_W + GAP;
                    g.setColor(Color.BLACK);
                    if (isDoubly) {
                        arrow(g, x1, y + NODE_H / 3, x2, y + NODE_H / 3);       // next
                        arrow(g, x2, y + 2 * NODE_H / 3, x1, y + 2 * NODE_H / 3); // prev
                    } else {
                        arrow(g, x + NODE_W - 12, y + NODE_H / 2, x2, y + NODE_H / 2);
                    }
                }
            }
            //end markers
            int lastX = MARGIN + (values.length - 1) * (NODE_W + GAP);
            g.setColor(Color.GRAY);
            String nullText = "null";
            g.drawString(nullText, lastX + NODE_W + 12, y + NODE_H / 2 + 5);
            g.setColor(Color.BLACK);
            arrow(g, lastX + NODE_W - (isDoubly ? 9 : 12), y + NODE_H / 2, lastX + NODE_W + 10, y + NODE_H / 2);
            if (isDoubly) {
                g.setColor(Color.GRAY);
                g.drawString("null", MARGIN - 34, y + NODE_H / 2 + 5);
                g.setColor(Color.BLACK);
                arrow(g, MARGIN + 9, y + NODE_H / 2, MARGIN - 4, y + NODE_H / 2);
            }
            //head / tail labels
            label(g, "HEAD", MARGIN + NODE_W / 2, y - 14, new Color(30, 90, 160));
            if (isDoubly) label(g, "TAIL", lastX + NODE_W / 2, y - (values.length == 1 ? 34 : 14), new Color(200, 100, 20));
        }

        private void label(Graphics2D g, String text, int cx, int y, Color color) {
            g.setColor(color);
            g.drawString(text, cx - g.getFontMetrics().stringWidth(text) / 2, y);
            g.drawLine(cx, y + 3, cx, y + 10);
        }

        private void center(Graphics2D g, String text, int xLeft, int xRight, int baseline) {
            g.setColor(g.getColor());
            int w = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (xLeft + xRight) / 2 - w / 2, baseline);
        }

        private void arrow(Graphics2D g, int x1, int y1, int x2, int y2) {
            g.drawLine(x1, y1, x2, y2);
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int len = 8;
            g.drawLine(x2, y2, (int) (x2 - len * Math.cos(angle - 0.4)), (int) (y2 - len * Math.sin(angle - 0.4)));
            g.drawLine(x2, y2, (int) (x2 - len * Math.cos(angle + 0.4)), (int) (y2 - len * Math.sin(angle + 0.4)));
        }
    }
}
