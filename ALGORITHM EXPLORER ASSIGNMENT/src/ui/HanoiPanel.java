package ui;

import core.Metrics;
import recursion.HanoiMove;
import recursion.TowerOfHanoi;
import structures.CustomStack;

import javax.swing.*;
import java.awt.*;

/**
 * Module A(c): animated Tower of Hanoi. The solution is computed first by the recursive back end;
  this panel then replays the moves with Start, Pause, Step and Reset controls and an adjustable speed.
 */
class HanoiPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int TICK_MS = 15;

    private final JSpinner disksSpinner = new JSpinner(new SpinnerNumberModel(3, 1, TowerOfHanoi.MAX_DISKS, 1));
    private final JComboBox<String> sourceBox = new JComboBox<>(new String[]{"A", "B", "C"});
    private final JComboBox<String> destBox = new JComboBox<>(new String[]{"A", "B", "C"});
    private final JComboBox<String> auxBox = new JComboBox<>(new String[]{"A", "B", "C"});
    private final JButton startButton = UiUtil.button("Start");
    private final JButton pauseButton = UiUtil.button("Pause");
    private final JButton stepButton = UiUtil.button("Step");
    private final JButton resetButton = UiUtil.button("Reset");
    private final JSlider speedSlider = new JSlider(1, 10, 5);
    private final HanoiCanvas canvas = new HanoiCanvas();
    private final JTextArea moveList = UiUtil.textArea(10, 22);
    private final JLabel counter = UiUtil.messageLabel();
    private final JLabel message = UiUtil.messageLabel();
    private final Timer timer;

    private transient HanoiMove[] moves = new HanoiMove[0];
    private transient CustomStack[] pegs = new CustomStack[3];
    private int disks;
    private int nextMove;          // index of the move that is animating or will animate next
    private double progress;       // 0..1 progress of the current move
    private boolean animating;     // a move is in flight
    private boolean playing;       // keep going after each move
    private boolean validSetup;

    HanoiPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
//Default peg assignment A= source ,B = auxiliary and C = destination
        destBox.setSelectedIndex(2);
        auxBox.setSelectedIndex(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(3);
        speedSlider.setPreferredSize(new Dimension(130, 40));

        JPanel setup = UiUtil.row(new JLabel("Disks (1-" + TowerOfHanoi.MAX_DISKS + "):"), disksSpinner,
                new JLabel("Source:"), sourceBox, new JLabel("Destination:"), destBox, new JLabel("Auxiliary:"), auxBox);
        JPanel controls = UiUtil.row(startButton, pauseButton, stepButton, resetButton,
                new JLabel("   Speed (slow-fast):"), speedSlider);
        JPanel north = new JPanel(new GridLayout(2, 1));
        north.add(setup);
        north.add(controls);
        add(north, BorderLayout.NORTH);

        add(canvas, BorderLayout.CENTER);

        JScrollPane listScroll = new JScrollPane(moveList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Moves (recursive solution)"));
        listScroll.setPreferredSize(new Dimension(230, 200));
        add(listScroll, BorderLayout.EAST);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(counter);
        south.add(message);
        add(south, BorderLayout.SOUTH);

        timer = new Timer(TICK_MS, e -> tick());
        startButton.addActionListener(e -> start());
        pauseButton.addActionListener(e -> pause());
        stepButton.addActionListener(e -> step());
        resetButton.addActionListener(e -> reset());
        //Any change to the disk count or peg roles invalidates the current replay  and recomputes
        disksSpinner.addChangeListener(e -> reset());
        sourceBox.addActionListener(e -> reset());
        destBox.addActionListener(e -> reset());
        auxBox.addActionListener(e -> reset());

        reset();
    }

    // controls
//Recomputes the recursive solution for the current settings and resets all disks  onto the source peg.
    private void reset() {
        timer.stop();
        playing = false;
        animating = false;
        progress = 0;
        nextMove = 0;
        disks = (Integer) disksSpinner.getValue();
        char src = ((String) sourceBox.getSelectedItem()).charAt(0);
        char dst = ((String) destBox.getSelectedItem()).charAt(0);
        char aux = ((String) auxBox.getSelectedItem()).charAt(0);

        try {
            moves = TowerOfHanoi.solve(disks, src, dst, aux, new Metrics());
            validSetup = true;
        } catch (IllegalArgumentException ex) {
            validSetup = false;
            moves = new HanoiMove[0];
            moveList.setText("");
            UiUtil.show(message, ex.getMessage() + " Choose a different peg for each role.", UiUtil.ERROR);
        }

        for (int p = 0; p < 3; p++) pegs[p] = new CustomStack(disks);
        if (validSetup) {
            //Stacks disks largest-first so disk1(the smallest) sits on top
            for (int d = disks; d >= 1; d--) pegs[src - 'A'].push(d);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < moves.length; i++) sb.append(i + 1).append(". ").append(moves[i]).append('\n');
            moveList.getHighlighter().removeAllHighlights();
            moveList.setText(sb.toString());
            moveList.setCaretPosition(0);
            UiUtil.show(message, "Ready. The recursive solution has " + moves.length + " moves. Press Start or Step.", UiUtil.INFO);
        }
        String[] roleLabels = new String[3];
        for (int p = 0; p < 3; p++) roleLabels[p] = roleOf((char) ('A' + p));
        canvas.setState(pegs, disks, roleLabels);
        updateCounter();
        updateButtons();
    }

    private String roleOf(char peg) {
        if (((String) sourceBox.getSelectedItem()).charAt(0) == peg) return "source";
        if (((String) destBox.getSelectedItem()).charAt(0) == peg) return "destination";
        if (((String) auxBox.getSelectedItem()).charAt(0) == peg) return "auxiliary";
        return "";
    }

    private void start() {
        if (!validSetup) return;
        //If already at the end , restart from the beginning immediately
        if (nextMove >= moves.length) reset();
        playing = true;
        if (!animating) beginMove();
        timer.start();
        updateButtons();
    }

    private void pause() {
        playing = false;
        // an animation in progress finishes its current move, then stops
        updateButtons();
    }

    private void step() {
        if (!validSetup || animating || nextMove >= moves.length) return;
        playing = false;
        beginMove();
        timer.start();
        updateButtons();
    }

    //  animation

    private void beginMove() {
        if (nextMove >= moves.length) return;
        animating = true;
        progress = 0;
        canvas.setMoving(moves[nextMove], 0);
    }
//Advance the current disk animation by one timer tick until progress reaches 1.0
    private void tick() {
        if (!animating) {
            timer.stop();
            return;
        }
        double duration = 1150 - speedSlider.getValue() * 110; // 1040 ms (slow) down to 50 ms (fast) per move
        progress += TICK_MS / duration;
        if (progress < 1) {
            canvas.setMoving(moves[nextMove], progress);
            return;
        }
        finishMove();
    }
//Commit  the completed animation move to the underlying peg stacks and queues the next move if playing
    private void finishMove() {
        HanoiMove mv = moves[nextMove];
        int disk = pegs[mv.from - 'A'].pop();
        if (disk != mv.disk) throw new IllegalStateException("Internal error: wrong disk on top of peg " + mv.from);
        pegs[mv.to - 'A'].push(disk);
        nextMove++;
        animating = false;
        highlightMoveInList();
        canvas.setState(pegs, disks, currentRoles());
        updateCounter();

        if (nextMove >= moves.length) {
            playing = false;
            timer.stop();
            long expected = (1L << disks) - 1;
            boolean ok = moves.length == expected;
            UiUtil.show(message, "Finished. Total moves = " + moves.length + ", and 2^" + disks + " - 1 = " + expected
                    + (ok ? ": the counts match." : ": MISMATCH!"), ok ? UiUtil.OK : UiUtil.ERROR);
        } else if (playing) {
            UiUtil.show(message, "Playing... " + moves[nextMove - 1], UiUtil.INFO);
            beginMove();
        } else {
            timer.stop();
            UiUtil.show(message, "Paused after: " + moves[nextMove - 1], UiUtil.INFO);
        }
        updateButtons();
    }

    private String[] currentRoles() {
        String[] r = new String[3];
        for (int p = 0; p < 3; p++) r[p] = roleOf((char) ('A' + p));
        return r;
    }
//highlights the line corresponding  to the move that just finished and scrolls it into view
    private void highlightMoveInList() {
        try {
            int line = nextMove - 1;
            int start = moveList.getLineStartOffset(line);
            int end = moveList.getLineEndOffset(line);
            moveList.getHighlighter().removeAllHighlights();
            moveList.getHighlighter().addHighlight(start, end,
                    new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(new Color(255, 235, 120)));
            moveList.setCaretPosition(start);
        } catch (javax.swing.text.BadLocationException ignored) {
            // the line does not exist: nothing to highlight
        }
    }

    private void updateCounter() {
        long expected = (1L << disks) - 1;
        UiUtil.show(counter, "Moves made: " + nextMove + " of " + (validSetup ? moves.length : 0)
                + "      (expected total 2^" + disks + " - 1 = " + expected + ")", UiUtil.INFO);
    }
// Locks setup inputs  during playback and enables only valid transport buttons  for the current state
    private void updateButtons() {
        boolean finished = validSetup && nextMove >= moves.length;
        boolean running = playing || animating;
        startButton.setEnabled(validSetup && !playing);
        pauseButton.setEnabled(playing);
        stepButton.setEnabled(validSetup && !animating && !finished);
        disksSpinner.setEnabled(!running);
        sourceBox.setEnabled(!running);
        destBox.setEnabled(!running);
        auxBox.setEnabled(!running);
        resetButton.setEnabled(true);
    }
}
