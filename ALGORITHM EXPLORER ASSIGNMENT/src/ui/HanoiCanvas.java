package ui;

import recursion.HanoiMove;
import structures.CustomStack;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

// Draws the three pegs and the disks, including the disk that is currently moving
class HanoiCanvas extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient CustomStack[] pegs = new CustomStack[0];
    private int disks = 3;
    private final String[] roles = {"A", "B", "C"};

    // animation of one move: progress runs from 0 to 1
    private transient HanoiMove moving;
    private double progress;

    HanoiCanvas() {
        setPreferredSize(new Dimension(620, 360));
        setBackground(Color.WHITE);
    }
//Updates the static peg stacks and clears any active move animations
    void setState(CustomStack[] pegs, int disks, String[] roleLabels) {
        this.pegs = pegs;
        this.disks = disks;
        for (int i = 0; i < 3; i++) roles[i] = roleLabels[i];
        this.moving = null;
        repaint();
    }
//Updates the in-flight disk position for  the current animation frame
    void setMoving(HanoiMove move, double progress) {
        this.moving = move;
        this.progress = progress;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int baseY = h - 50;
        int pegHeight = Math.min(h - 120, 40 + disks * 24);
        int diskHeight = Math.min(22, (pegHeight - 10) / Math.max(disks, 1));
        int slot = w / 3;
        int maxDiskWidth = slot - 30;
//Draw wooden base platform and the three vertical pegs  with role labels underneath
        g.setColor(new Color(110, 80, 50));
        g.fillRoundRect(10, baseY, w - 20, 12, 8, 8);
        for (int p = 0; p < 3; p++) {
            int cx = slot * p + slot / 2;
            g.setColor(new Color(110, 80, 50));
            g.fillRoundRect(cx - 4, baseY - pegHeight, 8, pegHeight, 6, 6);
            g.setColor(Color.DARK_GRAY);
            g.setFont(getFont().deriveFont(Font.BOLD, 13f));
            String label = "Peg " + (char) ('A' + p) + " (" + roles[p] + ")";
            int lw = g.getFontMetrics().stringWidth(label);
            g.drawString(label, cx - lw / 2, baseY + 32);
        }

        // resting disks
        for (int p = 0; p < pegs.length; p++) {
            int cx = slot * p + slot / 2;
            for (int level = 0; level < pegs[p].size(); level++) {
                // the moving disk has already been taken off its source peg only when the move is applied,
                // so skip the top disk of the source peg while it is animating
                boolean isMovingDisk = moving != null && p == moving.from - 'A'
                        && level == pegs[p].size() - 1;
                if (isMovingDisk) continue;
                int disk = pegs[p].get(level);
                drawDisk(g, cx, baseY - (level + 1) * diskHeight, disk, maxDiskWidth, diskHeight);
            }
        }

        // moving disk with a 3-phase path lift, slide, drop
        if (moving != null) {
            int fromPeg = moving.from - 'A';
            int toPeg = moving.to - 'A';
            int fromX = slot * fromPeg + slot / 2;
            int toX = slot * toPeg + slot / 2;
            int fromY = baseY - pegs[fromPeg].size() * diskHeight;
            int toY = baseY - (pegs[toPeg].size() + 1) * diskHeight;
            int topY = baseY - pegHeight - diskHeight - 6;
            double x, y;
            if (progress < 0.25) {
                double t = progress / 0.25;
                x = fromX;
                y = fromY + (topY - fromY) * t;
            } else if (progress < 0.75) {
                double t = (progress - 0.25) / 0.5;
                x = fromX + (toX - fromX) * t;
                y = topY;
            } else {
                double t = (progress - 0.75) / 0.25;
                x = toX;
                y = topY + (toY - topY) * t;
            }
            drawDisk(g, (int) Math.round(x), (int) Math.round(y), moving.disk, maxDiskWidth, diskHeight);
        }
    }
//Renders a single disk centered at cx with width and HSB hue scaled by the disk's size
    private void drawDisk(Graphics2D g, int cx, int y, int disk, int maxWidth, int height) {
        int minWidth = 36;
        int width = disks <= 1 ? maxWidth : minWidth + (maxWidth - minWidth) * (disk - 1) / (disks - 1);
        //Shifts hue across the spectrum by disk size
        Color fill = new Color(211,211,211);
        g.setColor(fill);
        g.fillRoundRect(cx - width / 2, y, width, height - 2, 10, 10);
        g.setColor(fill.darker());
        g.drawRoundRect(cx - width / 2, y, width, height - 2, 10, 10);
        g.setColor(Color.BLACK);
        g.setFont(getFont().deriveFont(Font.BOLD, 11f));
        String s = String.valueOf(disk);
        g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y + height - 7);
    }
}
