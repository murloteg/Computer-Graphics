package ru.nsu.bolotov.view.panel;

import ru.nsu.bolotov.model.PointPair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class DrawablePanel extends JPanel implements MouseListener {
    private int trackedX;
    private int trackedY;
    private boolean isDrawing = false;
    private boolean drawMode = false;
    private Color generalColor;
    private final java.util.List<PointPair> panelState = new LinkedList<>();

    public DrawablePanel() {
        super();
        generalColor = Color.BLACK;

        this.addMouseListener(this);
        this.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent event) {
                    if (isDrawing) {
                        Graphics g = event.getComponent().getGraphics();
                        g.setColor(generalColor);
                        g.drawLine(trackedX, trackedY, event.getX(), event.getY());
                        trackedX = event.getX();
                        trackedY = event.getY();
                        panelState.add(new PointPair(trackedX, trackedY, false, generalColor));
                    }
                }
            }
        );
    }

    public void setGeneralColor(Color newColor) {
        generalColor = newColor;
    }

    public void resetPanelState() {
        panelState.clear();
        paintComponent(this.getGraphics());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < panelState.size() - 1; ++i) {
            PointPair previousPair = panelState.get(i);
            PointPair nextPair = panelState.get(i + 1);
            g.setColor(nextPair.getSelectedColor());
            if (!previousPair.isBorderPoint()) {
                g.drawLine(previousPair.getX(), previousPair.getY(), nextPair.getX(), nextPair.getY());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        trackedX = e.getX();
        trackedY = e.getY();
        panelState.add(new PointPair(trackedX, trackedY, true, generalColor));
        isDrawing = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        panelState.remove(panelState.size() - 1);
        panelState.add(new PointPair(trackedX, trackedY, true, generalColor));
        isDrawing = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
