package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class LineChartPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color[] COLORS = {
        new Color(31, 119, 180), new Color(214, 39, 40), new Color(44, 160, 44),
        new Color(255, 127, 14), new Color(148, 103, 189), new Color(140, 86, 75)
    };

    private final String title;
    private String[] names = new String[0];
    private double[][] xs = new double[0][];
    private double[][] ys = new double[0][];

    LineChartPanel(String title) {
        this.title = title;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(520, 360));
    }

    void setData(String[] names, double[][] xs, double[][] ys) {
        this.names = names;
        this.xs = xs;
        this.ys = ys;
        repaint();
    }

    BufferedImage toImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        draw(g, width, height);
        g.dispose();
        return image;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        draw((Graphics2D) g0, getWidth(), getHeight());
    }

    private void draw(Graphics2D g, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.setColor(Color.BLACK);
        g.drawString(title, w / 2 - g.getFontMetrics().stringWidth(title) / 2, 22);

        int left = 70, right = w - 20, top = 40, bottom = h - 90;
        double maxX = 0, maxY = 0;
        for (int s = 0; s < names.length; s++) {
            for (int i = 0; i < xs[s].length; i++) {
                if (xs[s][i] > maxX) maxX = xs[s][i];
                if (ys[s][i] > maxY) maxY = ys[s][i];
            }
        }
        if (maxX == 0 || maxY == 0) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawString("Run the experiment to see the chart.", left, h / 2);
            return;
        }
        double yTop = niceCeiling(maxY);
        double xTop = niceCeiling(maxX);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        for (int i = 0; i <= 5; i++) {
            double v = yTop * i / 5;
            int y = bottom - (int) ((bottom - top) * i / 5.0);
            g.setColor(new Color(225, 225, 225));
            g.drawLine(left, y, right, y);
            g.setColor(Color.DARK_GRAY);
            String label = format(v);
            g.drawString(label, left - 8 - g.getFontMetrics().stringWidth(label), y + 4);
        }
        for (int i = 0; i <= 5; i++) {
            double v = xTop * i / 5;
            int x = left + (int) ((right - left) * i / 5.0);
            g.setColor(new Color(235, 235, 235));
            g.drawLine(x, top, x, bottom);
            g.setColor(Color.DARK_GRAY);
            String label = format(v);
            g.drawString(label, x - g.getFontMetrics().stringWidth(label) / 2, bottom + 16);
        }
        g.setColor(Color.BLACK);
        g.drawLine(left, bottom, right, bottom);
        g.drawLine(left, top, left, bottom);
        g.drawString("n (number of elements)", (left + right) / 2 - 55, bottom + 34);
        Graphics2D rot = (Graphics2D) g.create();
        rot.rotate(-Math.PI / 2);
        rot.drawString("average time (ms)", -(top + bottom) / 2 - 40, 18);
        rot.dispose();

        g.setStroke(new BasicStroke(2f));
        for (int s = 0; s < names.length; s++) {
            g.setColor(COLORS[s % COLORS.length]);
            int px = -1, py = -1;
            for (int i = 0; i < xs[s].length; i++) {
                int x = left + (int) ((right - left) * xs[s][i] / xTop);
                int y = bottom - (int) ((bottom - top) * ys[s][i] / yTop);
                if (px >= 0) g.drawLine(px, py, x, y);
                g.fillOval(x - 3, y - 3, 7, 7);
                px = x;
                py = y;
            }
        }
        g.setStroke(new BasicStroke(1f));

        int lx = left, ly = h - 38;
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        for (int s = 0; s < names.length; s++) {
            int itemW = 26 + g.getFontMetrics().stringWidth(names[s]) + 12;
            if (lx + itemW > right + 10) {
                lx = left;
                ly += 18;
            }
            g.setColor(COLORS[s % COLORS.length]);
            g.fillRect(lx, ly - 9, 14, 10);
            g.setColor(Color.BLACK);
            g.drawString(names[s], lx + 20, ly);
            lx += itemW;
        }
    }

    private static double niceCeiling(double v) {
        double exp = Math.pow(10, Math.floor(Math.log10(v)));
        double f = v / exp;
        double nice = f <= 1 ? 1 : f <= 2 ? 2 : f <= 2.5 ? 2.5 : f <= 5 ? 5 : 10;
        return nice * exp;
    }

    private static String format(double v) {
        if (v >= 1000) 
            return String.format("%,.0f", v);
        if (v >= 10) 
            return String.format("%.0f", v);
        if (v >= 1) 
         return String.format("%.1f", v);
        return String.format("%.2f", v);
    }
}
