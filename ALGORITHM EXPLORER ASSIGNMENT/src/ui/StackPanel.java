package ui;

import core.ArrayUtil;
import structures.CustomStack;

import javax.swing.*;
import java.awt.*;

class StackPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(6, 1, 12, 1));
    private final JTextField valueField = new JTextField("10", 6);
    private final JLabel message = UiUtil.messageLabel();
    private final Canvas canvas = new Canvas();
    private transient CustomStack stack = new CustomStack(6);

    StackPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton create = UiUtil.button("Create / reset stack");
        create.addActionListener(e -> create());
        JButton push = UiUtil.button("Push");
        push.addActionListener(e -> run(() -> {
            int v = readValue();
            stack.push(v);
            return "Pushed " + v;
        }));
        JButton pop = UiUtil.button("Pop");
        pop.addActionListener(e -> run(() -> "Popped " + stack.pop()));
        JButton peek = UiUtil.button("Peek");
        peek.addActionListener(e -> run(() -> "Top element is " + stack.peek() + " (not removed)"));
        JButton size = UiUtil.button("Size");
        size.addActionListener(e -> run(() -> "Size = " + stack.size() + " of " + stack.capacity()));
        JButton empty = UiUtil.button("is_empty");
        empty.addActionListener(e -> run(() -> "is_empty = " + stack.isEmpty()));
        JButton full = UiUtil.button("is_full");
        full.addActionListener(e -> run(() -> "is_full = " + stack.isFull()));
        JButton display = UiUtil.button("Display all");
        display.addActionListener(e -> run(() -> "Stack (bottom -> top): " + stack.display()));

        JPanel north = new JPanel(new GridLayout(2, 1));
        north.add(UiUtil.row(new JLabel("Capacity (1-12):"), capacitySpinner, create));
        north.add(UiUtil.row(new JLabel("Value:"), valueField, push, pop, peek, size, empty, full, display));
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        UiUtil.show(message, "A stack with capacity 6 was created. Elements are added and removed at the top (LIFO).", UiUtil.INFO);
    }

    private interface Action {
        String perform();
    }

    private int readValue() {
        return ArrayUtil.parseInt(valueField.getText(), "Value", Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private void create() {
        stack = new CustomStack((Integer) capacitySpinner.getValue());
        canvas.repaint();
        UiUtil.show(message, "New empty stack created with capacity " + stack.capacity() + ".", UiUtil.INFO);
    }

    private void run(Action action) {
        try {
            String text = action.perform();
            UiUtil.show(message, text, UiUtil.OK);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
        }
        canvas.repaint();
    }

    /** Draws the array as a vertical column of slots, bottom slot = index 0. */
    private class Canvas extends JPanel {
        private static final long serialVersionUID = 1L;

        Canvas() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(400, 420));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cap = stack.capacity();
            int cellH = Math.min(46, (getHeight() - 60) / cap);
            int cellW = 120;
            int x = getWidth() / 2 - cellW / 2;
            int bottom = getHeight() - 30;
            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            for (int i = 0; i < cap; i++) {
                int y = bottom - (i + 1) * cellH;
                boolean used = i < stack.size();
                g.setColor(used ? new Color(190, 220, 250) : new Color(245, 245, 245));
                g.fillRect(x, y, cellW, cellH);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, cellW, cellH);
                g.setColor(Color.GRAY);
                g.drawString("[" + i + "]", x - 40, y + cellH / 2 + 5);
                if (used) {
                    g.setColor(Color.BLACK);
                    String v = String.valueOf(stack.get(i));
                    g.drawString(v, x + cellW / 2 - g.getFontMetrics().stringWidth(v) / 2, y + cellH / 2 + 5);
                }
            }
            if (!stack.isEmpty()) {
                int y = bottom - stack.size() * cellH + cellH / 2;
                g.setColor(new Color(200, 60, 40));
                g.drawString("TOP \u2192", x + cellW + 12, y + 5);
            } else {
                g.setColor(new Color(200, 60, 40));
                g.drawString("top = -1 (empty)", x + cellW + 12, bottom - 5);
            }
        }
    }
}
