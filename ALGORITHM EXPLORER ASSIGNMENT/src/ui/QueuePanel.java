package ui;

import core.ArrayUtil;
import structures.ArrayQueue;
import structures.CircularQueue;
import structures.LinearQueue;

import javax.swing.*;
import java.awt.*;


class QueuePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final boolean circular;
    private final JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JTextField valueField = new JTextField("10", 6);
    private final JLabel message = UiUtil.messageLabel();
    private final JLabel indices = UiUtil.messageLabel();
    private final Canvas canvas = new Canvas();
    private transient ArrayQueue queue;
    private int nextAutoValue = 10;

    QueuePanel(boolean circular) {
        this.circular = circular;
        queue = newQueue(5);
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton create = UiUtil.button("Create / reset queue");
        create.addActionListener(e -> create());
        JButton enqueue = UiUtil.button("Enqueue");
        enqueue.addActionListener(e -> run(() -> {
            int v = ArrayUtil.parseInt(valueField.getText(), "Value", Integer.MIN_VALUE, Integer.MAX_VALUE);
            queue.enqueue(v);
            return "Enqueued " + v;
        }));
        JButton dequeue = UiUtil.button("Dequeue");
        dequeue.addActionListener(e -> run(() -> "Dequeued " + queue.dequeue()));
        JButton front = UiUtil.button("Front");
        front.addActionListener(e -> run(() -> "Front element is " + queue.front() + " (not removed)"));
        JButton size = UiUtil.button("Size");
        size.addActionListener(e -> run(() -> "Size = " + queue.size() + " of " + queue.capacity()));
        JButton empty = UiUtil.button("is_empty");
        empty.addActionListener(e -> run(() -> "is_empty = " + queue.isEmpty()));
        JButton full = UiUtil.button("is_full");
        full.addActionListener(e -> run(() -> "is_full = " + queue.isFull()
                + (!circular && queue.isFull() && queue.frontIndex() > 0
                        ? " (this is the FALSE FULL problem: " + queue.frontIndex() + " slot(s) at the start are empty)" : "")));
        JButton display = UiUtil.button("Display all");
        display.addActionListener(e -> run(() -> "Queue (front -> rear): " + queue.display()));

        JPanel north = new JPanel(new GridLayout(circular ? 2 : 3, 1));
        north.add(UiUtil.row(new JLabel("Capacity (1-10):"), capacitySpinner, create));
        north.add(UiUtil.row(new JLabel("Value:"), valueField, enqueue, dequeue, front, size, empty, full, display));
        if (!circular) {
            JButton demo = UiUtil.button("Demo: false full");
            demo.addActionListener(e -> demoFalseFull());
            north.add(UiUtil.row(demo, new JLabel("Fills the queue, dequeues 2 elements, then tries to enqueue again.")));
        }
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JScrollPane(canvas), BorderLayout.CENTER);
        center.add(indices, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JTextArea explain = UiUtil.textArea(4, 60);
        explain.setText(circular
                ? "Circular queue: front and rear wrap around with  index = (index + 1) % capacity.  After dequeues free the first slots, "
                  + "the rear moves on to index 0 and reuses them. The queue is full only when all " + "slots really hold elements, so there is no false full."
                : "Linear queue: front and rear only move forward. A dequeue frees a slot at the front but that slot can never be used again. "
                  + "When rear reaches the last index the queue reports full even if slots at the start are empty. This is the \"false full\" problem. "
                  + "The Circular Queue tab solves it by wrapping rear around to index 0.");
        JScrollPane explainScroll = new JScrollPane(explain);
        explainScroll.setBorder(BorderFactory.createTitledBorder(circular ? "How the circular queue works" : "The false full problem"));
        JPanel south = new JPanel(new BorderLayout());
        south.add(message, BorderLayout.NORTH);
        south.add(explainScroll, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        UiUtil.show(message, "A " + (circular ? "circular" : "linear") + " queue with capacity 5 was created. "
                + "Elements join at the rear and leave from the front (FIFO).", UiUtil.INFO);
        updateIndices();
    }

    private ArrayQueue newQueue(int capacity) {
        return circular ? new CircularQueue(capacity) : new LinearQueue(capacity);
    }

    private interface Action {
        String perform();
    }

    private void create() {
        queue = newQueue((Integer) capacitySpinner.getValue());
        UiUtil.show(message, "New empty queue created with capacity " + queue.capacity() + ".", UiUtil.INFO);
        updateIndices();
        canvas.repaint();
    }

    private void run(Action action) {
        try {
            UiUtil.show(message, action.perform(), UiUtil.OK);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            UiUtil.show(message, ex.getMessage(), UiUtil.ERROR);
        }
        updateIndices();
        canvas.repaint();
    }

    private void demoFalseFull() {
        int cap = Math.max(3, (Integer) capacitySpinner.getValue());
        capacitySpinner.setValue(cap);
        queue = newQueue(cap);
        for (int i = 0; i < cap; i++) queue.enqueue(10 * (i + 1));
        queue.dequeue();
        queue.dequeue();
        updateIndices();
        canvas.repaint();
        try {
            queue.enqueue(99);
            UiUtil.show(message, "Enqueued 99.", UiUtil.OK);
        } catch (IllegalStateException ex) {
            UiUtil.show(message, "Demo: filled " + cap + " slots, dequeued 2, then tried to enqueue 99. " + ex.getMessage(), UiUtil.ERROR);
        }
    }

    private void updateIndices() {
        String rear = queue.rearIndex() < 0 ? "none yet" : String.valueOf(queue.rearIndex());
        UiUtil.show(indices, "front index = " + queue.frontIndex() + "     rear index = " + rear
                + "     size = " + queue.size() + " / " + queue.capacity(), UiUtil.INFO);
    }

    private class Canvas extends JPanel {
        private static final long serialVersionUID = 1L;

        Canvas() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(700, 200));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cap = queue.capacity();
            int cellW = Math.min(80, (getWidth() - 40) / cap);
            int cellH = 60;
            int x0 = (getWidth() - cap * cellW) / 2;
            int y = getHeight() / 2 - cellH / 2;
            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            for (int i = 0; i < cap; i++) {
                int x = x0 + i * cellW;
                boolean used = queue.isOccupied(i);
                g.setColor(used ? new Color(190, 235, 200) : new Color(245, 245, 245));
                g.fillRect(x, y, cellW, cellH);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, cellW, cellH);
                g.setColor(Color.GRAY);
                String idx = "[" + i + "]";
                g.drawString(idx, x + cellW / 2 - g.getFontMetrics().stringWidth(idx) / 2, y + cellH + 20);
                if (used) {
                    g.setColor(Color.BLACK);
                    String v = String.valueOf(queue.valueAt(i));
                    g.drawString(v, x + cellW / 2 - g.getFontMetrics().stringWidth(v) / 2, y + cellH / 2 + 5);
                } else if (!circular && i < queue.frontIndex()) {
                    g.setColor(new Color(200, 60, 40));
                    String wasted = "unused";
                    g.setFont(getFont().deriveFont(Font.PLAIN, 11f));
                    g.drawString(wasted, x + cellW / 2 - g.getFontMetrics().stringWidth(wasted) / 2, y + cellH / 2 + 4);
                    g.setFont(getFont().deriveFont(Font.BOLD, 14f));
                }
            }
            if (!queue.isEmpty()) {
                marker(g, x0 + queue.frontIndex() * cellW + cellW / 2, y - 12, "FRONT", new Color(30, 90, 160));
                marker(g, x0 + queue.rearIndex() * cellW + cellW / 2, y - 12, "REAR", new Color(200, 100, 20));
            }
        }

        private void marker(Graphics2D g, int cx, int y, String text, Color color) {
            boolean same = queue.frontIndex() == queue.rearIndex();
            int offset = (same && text.equals("REAR")) ? -18 : 0;
            g.setColor(color);
            g.drawString(text, cx - g.getFontMetrics().stringWidth(text) / 2, y + offset);
            g.drawLine(cx, y + offset + 3, cx, y + 12);
        }
    }
}
