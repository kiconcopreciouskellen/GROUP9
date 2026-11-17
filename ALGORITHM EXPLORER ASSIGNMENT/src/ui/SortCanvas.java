package ui;

import sorting.SortTracer;

import javax.swing.*;
import java.awt.*;

//Draws one frame of a sort as colored bars.
class SortCanvas extends JPanel {
    private static final long serialVersionUID = 1L;

    private transient SortTracer.Frame frame;
    private String caption = "Press \"Run sort\" to load the array, then use Step or Auto-play.";

    SortCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 300));
    }
//Updates  the displayed snapshot  and triggers a  repaint
    void show(SortTracer.Frame frame, String caption) {
        this.frame = frame;
        this.caption = caption;
        repaint();
    }
// Maps a SortTracer state mark to its bar fill color
    private static Color colorFor(byte mark) {
        switch (mark) {
            case SortTracer.COMPARE: return new Color(250, 200, 60);
            case SortTracer.SWAP:    return new Color(235, 100, 90);
            case SortTracer.SORTED:  return new Color(110, 190, 120);
            case SortTracer.PIVOT:   return new Color(150, 90, 200);
            case SortTracer.YELLOW:  return new Color(255, 235, 80);
            case SortTracer.GREEN:   return new Color(120, 210, 130);
            default:                 return new Color(150, 185, 235);
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(getFont().deriveFont(Font.BOLD, 13f));
        g.setColor(Color.DARK_GRAY);
        g.drawString(caption, 12, 20);

        // legend
        String[] names = {"compare", "swap / write", "sorted", "pivot", "yellow (< pivot)", "green (>= pivot)"};
        byte[] marks = {SortTracer.COMPARE, SortTracer.SWAP, SortTracer.SORTED, SortTracer.PIVOT, SortTracer.YELLOW, SortTracer.GREEN};
        int lx = 12;
        int ly = getHeight() - 12;
        g.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        for (int i = 0; i < names.length; i++) {
            g.setColor(colorFor(marks[i]));
            g.fillRect(lx, ly - 10, 12, 12);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(lx, ly - 10, 12, 12);
            g.drawString(names[i], lx + 16, ly);
            lx += 22 + g.getFontMetrics().stringWidth(names[i]) + 10;
        }
        if (frame == null) return;

        int n = frame.values.length;
        if (n == 0) return;
        // Find vlaue range so bar heights scale cleanly regardless of negative  or large numbers
        int min = frame.values[0], max = frame.values[0];
        for (int v : frame.values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        //Computes horizontal slot width and vertical drawing bounds
        int left = 20, right = getWidth() - 20, top = 40, bottom = getHeight() - 50;
        int slot = (right - left) / n;
        int barW = Math.max(4, slot - 6);
        int maxH = bottom - top - 18;
        g.setFont(getFont().deriveFont(Font.BOLD, slot >= 30 ? 12f : 9f));
        for (int i = 0; i < n; i++) {
            int v = frame.values[i];
            //Normalizes height between 24px and maxH
            double rel = max == min ? 1.0 : (double) (v - min) / (max - min);
            int h = (int) (24 + rel * (maxH - 24));
            int x = left + i * slot + (slot - barW) / 2;
            g.setColor(colorFor(frame.marks[i]));
            g.fillRect(x, bottom - h, barW, h);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, bottom - h, barW, h);
            //Centers value label above the br
            String s = String.valueOf(v);
            int sw = g.getFontMetrics().stringWidth(s);
            g.drawString(s, x + barW / 2 - sw / 2, bottom - h - 4);
            //Only draw index labels below bars when there's enough horizontal space to avoid overlap
            if (slot >= 22) {
                g.setColor(Color.GRAY);
                String idx = String.valueOf(i);
                g.drawString(idx, x + barW / 2 - g.getFontMetrics().stringWidth(idx) / 2, bottom + 14);
            }
        }
    }
}
