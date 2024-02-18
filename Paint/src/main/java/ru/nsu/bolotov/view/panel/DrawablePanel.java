package ru.nsu.bolotov.view.panel;

import ru.nsu.bolotov.model.data.SpanCoords;
import ru.nsu.bolotov.model.paintmode.PaintMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.CANVAS_SIZE;

public class DrawablePanel extends JPanel implements MouseListener {
    private int trackedX;
    private int trackedY;
    private PaintMode paintMode;
    private Color generalColor;
    private int lineSize;
    private final BufferedImage canvas;
    private short clicksCount;

    public DrawablePanel() {
        super();
        generalColor = Color.BLACK;
        lineSize = 1;
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
                            Graphics2D cg2 = (Graphics2D) canvasGraphics;
                            cg2.setStroke(new BasicStroke(lineSize));

                            canvasGraphics.drawLine(trackedX, trackedY, event.getX(), event.getY());
                            canvasGraphics.dispose();
                            SwingUtilities.updateComponentTreeUI(DrawablePanel.this.getParent());
                            trackedX = event.getX();
                            trackedY = event.getY();
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

    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
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
            if (lineSize == 1) {
                bresenhamLineAlgorithm(trackedX, trackedY, e.getX(), e.getY());
            } else {
                Graphics canvasGraphics = canvas.getGraphics();
                canvasGraphics.setColor(generalColor);
                Graphics2D cg2 = (Graphics2D) canvasGraphics;
                cg2.setStroke(new BasicStroke(lineSize));
                canvasGraphics.drawLine(trackedX, trackedY, e.getX(), e.getY());
                canvasGraphics.dispose();
            }
            SwingUtilities.updateComponentTreeUI(DrawablePanel.this.getParent());
            clicksCount = 0;
        } else if (paintMode == PaintMode.LINE && clicksCount == 0) {
            ++clicksCount;
        } else if (paintMode == PaintMode.FILL) {
            spanFillingAlgorithm(e.getX(), e.getY());
            SwingUtilities.updateComponentTreeUI(DrawablePanel.this.getParent());
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

    private boolean isSpanPoint(int x, int y, int oldColor) {
        return canvas.getRGB(x, y) == oldColor;
    }

    private SpanCoords findSpanByPoint(int x, int y, int oldColor) {
        SpanCoords spanCoords = new SpanCoords(x, x, y);
        int currentX = x;
        while (currentX > 0) {
            if (isSpanPoint(currentX, y, oldColor)) {
                --currentX;
            } else {
                break;
            }
        }
        if (currentX != x) {
            spanCoords.setLeftBorderX(currentX + 1);
        }
        currentX = x;
        while (currentX < CANVAS_SIZE) {
            if (isSpanPoint(currentX, y, oldColor)) {
                ++currentX;
            } else {
                break;
            }
        }
        if (currentX != x) {
            spanCoords.setRightBorderX(currentX - 1);
        }
        return spanCoords;
    }

    private void fillAllPointsFromSpanByNewColor(SpanCoords spanCoords, int newColor) {
        int y = spanCoords.getCoordY();
        for (int x = spanCoords.getLeftBorderX(); x <= spanCoords.getRightBorderX(); ++x) {
            canvas.setRGB(x, y, newColor);
        }
    }

    private boolean isSpanAlreadyHandled(SpanCoords spanCoords, int newColor) {
        return canvas.getRGB(spanCoords.getLeftBorderX(), spanCoords.getCoordY()) == newColor;
    }

    private boolean isSpanAlreadyInStack(SpanCoords currentSpan, LinkedList<SpanCoords> spanStack) {
        for (SpanCoords span : spanStack) {
            if (currentSpan.equals(span)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptySpan(SpanCoords currentSpan) {
        return currentSpan.getLeftBorderX() == currentSpan.getRightBorderX();
    }

    private void spanFillingAlgorithm(int xSeed, int ySeed) {
        int oldColor = canvas.getRGB(xSeed, ySeed);
        int newColor = generalColor.getRGB();
        if (oldColor == newColor) {
            return;
        }
        SpanCoords firstSpan = findSpanByPoint(xSeed, ySeed, oldColor);
        LinkedList<SpanCoords> spanStack = new LinkedList<>();
        spanStack.addLast(firstSpan);
        while (!spanStack.isEmpty()) {
            SpanCoords currentSpan = spanStack.removeFirst();
            if (isSpanAlreadyHandled(currentSpan, newColor)) {
                continue;
            }
            fillAllPointsFromSpanByNewColor(currentSpan, newColor);

            int currentY = currentSpan.getCoordY();
            int currentX = currentSpan.getLeftBorderX();
            while (currentX <= currentSpan.getRightBorderX()) {
                SpanCoords nextSpan = findSpanByPoint(currentX, currentY + 1, oldColor);
                if (isEmptySpan(nextSpan) || isSpanAlreadyInStack(nextSpan, spanStack)) {
                    currentX = nextSpan.getRightBorderX() + 1;
                    continue;
                }
                ++currentX;
                spanStack.addLast(nextSpan);
            }

            currentX = currentSpan.getLeftBorderX();
            while (currentX <= currentSpan.getRightBorderX()) {
                SpanCoords nextSpan = findSpanByPoint(currentX, currentY - 1, oldColor);
                if (isEmptySpan(nextSpan) || isSpanAlreadyInStack(nextSpan, spanStack)) {
                    currentX = nextSpan.getRightBorderX() + 1;
                    continue;
                }
                ++currentX;
                spanStack.addLast(nextSpan);
            }
        }
    }

    private void drawLineLow(int x0, int y0, int x1, int y1) {
        int diffX = x1 - x0;
        int diffY = y1 - y0;
        int yStep = 1;
        if (diffY < 0) {
            yStep = -1;
            diffY = -diffY;
        }

        int error = (2 * diffY) - diffX;
        int y = y0;
        for (int x = x0; x <= x1; ++x) {
            canvas.setRGB(x, y, generalColor.getRGB());
            if (error > 0) {
                y += yStep;
                error += 2 * (diffY - diffX);
            } else {
                error += 2 * diffY;
            }
        }
    }

    private void drawLineHigh(int x0, int y0, int x1, int y1) {
        int diffX = x1 - x0;
        int diffY = y1 - y0;
        int xStep = 1;
        if (diffX < 0) {
            xStep = -1;
            diffX = -diffX;
        }

        int error = (2 * diffX) - diffY;
        int x = x0;
        for (int y = y0; y <= y1; ++y) {
            canvas.setRGB(x, y, generalColor.getRGB());
            if (error > 0) {
                x += xStep;
                error += 2 * (diffX - diffY);
            } else {
                error += 2 * diffX;
            }
        }
    }

    private void bresenhamLineAlgorithm(int x0, int y0, int x1, int y1) {
        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1) {
                drawLineLow(x1, y1, x0, y0);
            } else {
                drawLineLow(x0, y0, x1, y1);
            }
        } else {
            if (y0 > y1) {
                drawLineHigh(x1, y1, x0, y0);
            } else {
                drawLineHigh(x0, y0, x1, y1);
            }
        }
    }
}
