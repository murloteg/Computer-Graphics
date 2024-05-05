package ru.nsu.bolotov.gui.bspline;

import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.DISTANCE_BETWEEN_AXIS_DASH_PX;
import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.SUPPORT_POINT_RADIUS_PX;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.MINIMAL_HEIGHT;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.MINIMAL_WIDTH;

public class EditorPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, PropertyChangeListener {
    private final transient ApplicationParameters applicationParameters;
    private final transient BSplineRepresentation bSplineRepresentation;
    private int currentDistanceBetweenAxisDashPx = DISTANCE_BETWEEN_AXIS_DASH_PX;
    private MovingMode movingMode = MovingMode.STOP_MOVING;
    private int currentXPosition = 0;
    private int currentYPosition = 0;
    private int activeSupportPointIndex = 0;
    JScrollPane scrollPane = new JScrollPane();

    public EditorPanel(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        this.applicationParameters = applicationParameters;
        this.bSplineRepresentation = bSplineRepresentation;
        bSplineRepresentation.addPropertyChangeListener(this);
        Dimension minimalDimension = new Dimension(MINIMAL_WIDTH, MINIMAL_HEIGHT);
        this.setPreferredSize(minimalDimension);
        this.setBackground(Color.BLACK);

        scrollPane.setMaximumSize(minimalDimension);
        scrollPane.setViewportView(this);
        scrollPane.revalidate();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("bSplineUpdated".equals(evt.getPropertyName())) {
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawAxis(graphics);
        drawSupportPointsForBSpline(graphics);
        drawBSpline(graphics);
    }

    public void changeMovingMode(MovingMode movingMode) {
        this.movingMode = movingMode;
    }

    public void normalizeImage() {
        this.currentDistanceBetweenAxisDashPx = DISTANCE_BETWEEN_AXIS_DASH_PX;
        repaint();
    }

    public void updateBSpline() {
        bSplineRepresentation.setNumberOfBSplinePartSegments(applicationParameters.getNumberOfBSplinePartSegments());
        bSplineRepresentation.createBSpline();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;
        if (e.getButton() == MouseEvent.BUTTON1) {
            bSplineRepresentation.addNewSupportPoint(new Point2D.Double((e.getX() - centerX) / currentDistanceBetweenAxisDashPx, (centerY - e.getY()) / currentDistanceBetweenAxisDashPx));
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            bSplineRepresentation.deleteSupportPointByIndex(activeSupportPointIndex);
        }
        activeSupportPointIndex = bSplineRepresentation.getSupportPoints().size() - 1;
        applicationParameters.setNumberOfSupportPoints(bSplineRepresentation.getSupportPoints().size());
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentXPosition = e.getX();
        currentYPosition = e.getY();
        checkPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        movingMode = MovingMode.STOP_MOVING;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;

        switch (movingMode) {
            case MOVE_BSPLINE_POINT -> {
                Point2D activePoint = bSplineRepresentation.getSupportPoints().get(activeSupportPointIndex);
                activePoint.setLocation((e.getX() - centerX) / currentDistanceBetweenAxisDashPx, (centerY - e.getY()) / currentDistanceBetweenAxisDashPx);
                bSplineRepresentation.createBSpline();
            }
            default -> {
                return;
            }
        }
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scale = e.getWheelRotation();
        if (currentDistanceBetweenAxisDashPx - scale > 5) {
            currentDistanceBetweenAxisDashPx -= scale;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private void drawAxis(Graphics g) {
        int height = this.getHeight();
        int width = this.getWidth();

        g.setColor(Color.WHITE);
        g.drawLine(0, height / 2, width, height / 2);
        g.drawLine(width / 2, 0, width / 2, height);

        int linesNumber = (width / 2) / currentDistanceBetweenAxisDashPx;
        for (int i = 1; i <= linesNumber; i++) {
            if (i % 5 == 0) {
                g.drawLine(width / 2 - currentDistanceBetweenAxisDashPx * i, height / 2 - 6, width / 2 - currentDistanceBetweenAxisDashPx * i, height / 2 + 6);
                g.drawLine(width / 2 + currentDistanceBetweenAxisDashPx * i, height / 2 - 6, width / 2 + currentDistanceBetweenAxisDashPx * i, height / 2 + 6);
            } else {
                g.drawLine(width / 2 - currentDistanceBetweenAxisDashPx * i, height / 2 - 3, width / 2 - currentDistanceBetweenAxisDashPx * i, height / 2 + 3);
                g.drawLine(width / 2 + currentDistanceBetweenAxisDashPx * i, height / 2 - 3, width / 2 + currentDistanceBetweenAxisDashPx * i, height / 2 + 3);
            }
        }

        linesNumber = (height / 2) / currentDistanceBetweenAxisDashPx;
        for (int i = 1; i <= linesNumber; i++) {
            if (i % 5 == 0) {
                g.drawLine(width / 2 - 6, height / 2 - currentDistanceBetweenAxisDashPx * i, width / 2 + 6, height / 2 - currentDistanceBetweenAxisDashPx * i);
                g.drawLine(width / 2 - 6, height / 2 + currentDistanceBetweenAxisDashPx * i, width / 2 + 6, height / 2 + currentDistanceBetweenAxisDashPx * i);
            } else {
                g.drawLine(width / 2 - 3, height / 2 - currentDistanceBetweenAxisDashPx * i, width / 2 + 3, height / 2 - currentDistanceBetweenAxisDashPx * i);
                g.drawLine(width / 2 - 3, height / 2 + currentDistanceBetweenAxisDashPx * i, width / 2 + 3, height / 2 + currentDistanceBetweenAxisDashPx * i);
            }
        }
    }

    private void drawSupportPointsForBSpline(Graphics g) {
        Color activePointColor = Color.decode("#12D4A2");
        Color inactivePointColor = Color.decode("#FFFF00");
        g.setColor(inactivePointColor);
        List<Point2D> points = bSplineRepresentation.getSupportPoints();

        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;
        for (int i = 0; i < points.size(); ++i) {
            Point2D p = points.get(i);
            g.setColor(inactivePointColor);
            if (i == activeSupportPointIndex) {
                g.setColor(activePointColor);
            }
            g.drawOval((int) (centerX + p.getX() * currentDistanceBetweenAxisDashPx - SUPPORT_POINT_RADIUS_PX / 2.0),
                    (int) (centerY - p.getY() * currentDistanceBetweenAxisDashPx - SUPPORT_POINT_RADIUS_PX / 2.0),
                    SUPPORT_POINT_RADIUS_PX,
                    SUPPORT_POINT_RADIUS_PX
            );
        }

        g.setColor(Color.WHITE);
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D p1 = convertToScreen(points.get(i));
            Point2D p2 = convertToScreen(points.get(i + 1));

            g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        }
    }

    private void drawBSpline(Graphics g) {
        Color orange = Color.decode("#FF5520");
        g.setColor(orange);
        List<Point2D> points = bSplineRepresentation.getBSplinePoints();
        if (points.size() < 4) {
            return;
        }

        for (int i = 0; i < points.size() - 1; ++i) {
            Point2D p1 = convertToScreen(points.get(i));
            Point2D p2 = convertToScreen(points.get(i + 1));

            g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        }
    }

    private Point2D convertToScreen(Point2D p) {
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;
        double x = centerX + p.getX() * currentDistanceBetweenAxisDashPx;
        double y = centerY - p.getY() * currentDistanceBetweenAxisDashPx;
        return new Point2D.Double(x, y);
    }

    private void checkPoint() {
        List<Point2D> anchorPoints = bSplineRepresentation.getSupportPoints();
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;

        int index = 0;
        for (Point2D p : anchorPoints) {
            double globalX = centerX + p.getX() * currentDistanceBetweenAxisDashPx;
            double globalY = centerY - p.getY() * currentDistanceBetweenAxisDashPx;
            if (currentXPosition <= globalX + SUPPORT_POINT_RADIUS_PX && currentXPosition >= globalX - SUPPORT_POINT_RADIUS_PX &&
                    currentYPosition <= globalY + SUPPORT_POINT_RADIUS_PX && currentYPosition >= globalY - SUPPORT_POINT_RADIUS_PX) {
                activeSupportPointIndex = index;
                movingMode = MovingMode.MOVE_BSPLINE_POINT;
                break;
            }
            ++index;
        }
    }
}
