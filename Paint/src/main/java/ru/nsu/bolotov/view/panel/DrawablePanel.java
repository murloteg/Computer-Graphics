package ru.nsu.bolotov.view.panel;

import ru.nsu.bolotov.model.paintmode.PaintMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.CANVAS_SIZE;

public class DrawablePanel extends JPanel implements MouseListener {
    private int trackedX;
    private int trackedY;
    private PaintMode paintMode;
    private Color generalColor;
    private final BufferedImage canvas;
    private short clicksCount;

    public DrawablePanel() {
        super();
        generalColor = Color.BLACK;
        canvas = new BufferedImage(CANVAS_SIZE, CANVAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        canvas.getGraphics().fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        paintMode = PaintMode.BRUSH;
        clicksCount = 0;

        this.addMouseListener(this);
        this.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent event) {
                    switch (paintMode) {
                        case BRUSH: {
                            Graphics canvasGraphics = canvas.getGraphics();
                            canvasGraphics.setColor(generalColor);
                            canvasGraphics.drawLine(trackedX, trackedY, event.getX(), event.getY());
                            canvasGraphics.dispose();
                            SwingUtilities.updateComponentTreeUI(DrawablePanel.this.getParent());
                            trackedX = event.getX();
                            trackedY = event.getY();
                            break;
                        }
                        case LINE: {

                        }
                        case FILL: {
                            break;
                        }
                        case POLYGON: {
                            break;
                        }
                    }
                }
            }
        );
    }

    public void setGeneralColor(Color newColor) {
        this.generalColor = newColor;
    }

    public void setPaintMode(PaintMode paintMode) {
        this.paintMode = paintMode;
    }

    public void resetPanelState() {
        canvas.getGraphics().fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        SwingUtilities.updateComponentTreeUI(this.getParent());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (paintMode == PaintMode.LINE && clicksCount == 1) {
            Graphics canvasGraphics = canvas.getGraphics();
            canvasGraphics.setColor(generalColor);
            // TODO: use Bresenham's algorithm
            canvasGraphics.drawLine(trackedX, trackedY, e.getX(), e.getY());
            canvasGraphics.dispose();
            SwingUtilities.updateComponentTreeUI(DrawablePanel.this.getParent());
            clicksCount = 0;
        } else if (paintMode == PaintMode.LINE && clicksCount == 0) {
            ++clicksCount;
        } else {
            clicksCount = 0;
        }
        trackedX = e.getX();
        trackedY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
