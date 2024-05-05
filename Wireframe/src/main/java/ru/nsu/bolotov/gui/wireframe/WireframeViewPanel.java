package ru.nsu.bolotov.gui.wireframe;

import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.FourCoordinatesVector;
import ru.nsu.bolotov.model.WireframeRepresentation;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.MAXIMAL_WIREFRAME_ZOOM_PARAMETER;
import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.MINIMAL_WIREFRAME_ZOOM_PARAMETER;

public class WireframeViewPanel extends JPanel implements PropertyChangeListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private final transient BSplineRepresentation bSplineRepresentation;
    private final transient WireframeRepresentation wireframeRepresentation;
    private Point lastPoint;

    public WireframeViewPanel(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        this.bSplineRepresentation = bSplineRepresentation;
        bSplineRepresentation.addPropertyChangeListener(this);
        this.wireframeRepresentation = new WireframeRepresentation(applicationParameters);

        this.setBackground(Color.WHITE);
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

    // TODO: добавить оси XYZ
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Color.BLACK);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        double zoomParameter = wireframeRepresentation.getZoomParameter();
        // FIXME: учесть расстояние до точек (разная яркость)
        if (bSplineRepresentation.getSupportPoints().size() < 4) {
            Map<Integer, java.util.List<Point2D>> defaultWireframeExample = wireframeRepresentation.defaultWireframeExample();
            java.util.List<Point2D> examplePoints = defaultWireframeExample.get(0);
            java.util.List<Point2D> exampleEdges = defaultWireframeExample.get(1);

            for (Point2D point : exampleEdges) {
                graphics2D.drawLine(
                        (int) (centerX + examplePoints.get((int) point.getX()).getX() * zoomParameter), (int) (centerY - examplePoints.get((int) point.getX()).getY() * zoomParameter),
                        (int) (centerX + examplePoints.get((int) point.getY()).getX() * zoomParameter), (int) (centerY - examplePoints.get((int) point.getY()).getY() * zoomParameter)
                );
            }
        } else {
            wireframeRepresentation.createWireframePoints(bSplineRepresentation.getBSplinePoints());
            java.util.List<FourCoordinatesVector> points = wireframeRepresentation.getWireframeVectors();
            java.util.List<Integer> edges = wireframeRepresentation.getEdges();

            for (int i = 0; i < edges.size(); i += 2) {
                FourCoordinatesVector p1 = points.get(edges.get(i));
                FourCoordinatesVector p2 = points.get(edges.get(i + 1));

                graphics2D.drawLine(
                        (int) (centerX + p1.getX() * zoomParameter), (int) (centerY - p1.getY() * zoomParameter),
                        (int) (centerX + p2.getX() * zoomParameter), (int) (centerY - p2.getY() * zoomParameter)
                );
            }
        }
    }

    public void resetRotationMatrix() {
        wireframeRepresentation.resetRotationMatrix();
        lastPoint = new Point();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        wireframeRepresentation.updateRotationMatrix(lastPoint, e.getPoint());
        lastPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scale = e.getWheelRotation();
        double zoomParameter = wireframeRepresentation.getZoomParameter();
        double updateZoomValue = zoomParameter - scale;
        if (updateZoomValue > MINIMAL_WIREFRAME_ZOOM_PARAMETER && updateZoomValue < MAXIMAL_WIREFRAME_ZOOM_PARAMETER) {
            wireframeRepresentation.updateZoomParameter(zoomParameter - scale);
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
