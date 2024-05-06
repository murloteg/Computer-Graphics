package ru.nsu.bolotov.gui.wireframe;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.FourCoordinatesVector;
import ru.nsu.bolotov.model.Matrix;
import ru.nsu.bolotov.model.WireframeRepresentation;
import ru.nsu.bolotov.model.dto.ApplicationParametersDto;
import ru.nsu.bolotov.model.dto.BSplineStateDto;
import ru.nsu.bolotov.model.dto.ProgramStateDto;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.List;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.MAXIMAL_WIREFRAME_ZOOM_PARAMETER;
import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.MINIMAL_WIREFRAME_ZOOM_PARAMETER;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.*;

public class WireframeViewPanel extends JPanel implements PropertyChangeListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private final transient BSplineRepresentation bSplineRepresentation;
    private final transient WireframeRepresentation wireframeRepresentation;
    private final transient ApplicationParameters applicationParameters;
    private Point lastPoint;

    public WireframeViewPanel(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        this.bSplineRepresentation = bSplineRepresentation;
        bSplineRepresentation.addPropertyChangeListener(this);
        this.wireframeRepresentation = new WireframeRepresentation(applicationParameters);
        this.applicationParameters = applicationParameters;

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

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Color.BLACK);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        double zoomParameter = wireframeRepresentation.getZoomParameter();
        if (bSplineRepresentation.getSupportPoints().size() < 4) {
            Map<Integer, List<Point2D>> defaultWireframeExample = wireframeRepresentation.defaultWireframeExample();
            List<Point2D> examplePoints = defaultWireframeExample.get(0);
            List<Point2D> exampleEdges = defaultWireframeExample.get(1);

            double maxXValue = Double.MIN_VALUE;
            double maxYValue = Double.MIN_VALUE;
            for (Point2D point : examplePoints) {
                maxXValue = Math.max(maxXValue, point.getX());
                maxYValue = Math.max(maxYValue, point.getY());
            }

            for (Point2D point : exampleEdges) {
                double normalizedX1 = examplePoints.get((int) point.getX()).getX() / maxXValue;
                double normalizedY1 = examplePoints.get((int) point.getX()).getY() / maxYValue;
                double normalizedX2 = examplePoints.get((int) point.getY()).getX() / maxXValue;
//                double normalizedY2 = examplePoints.get((int) point.getY()).getY() / maxYValue;
                Color colorEdge = interpolateColor(normalizedX1, normalizedY1, normalizedX2);
                graphics2D.setColor(colorEdge);
                graphics2D.drawLine(
                        (int) (centerX + examplePoints.get((int) point.getX()).getX() * zoomParameter), (int) (centerY - examplePoints.get((int) point.getX()).getY() * zoomParameter),
                        (int) (centerX + examplePoints.get((int) point.getY()).getX() * zoomParameter), (int) (centerY - examplePoints.get((int) point.getY()).getY() * zoomParameter)
                );
            }
        } else {
            wireframeRepresentation.createWireframePoints(bSplineRepresentation.getBSplinePoints());
            List<FourCoordinatesVector> points = wireframeRepresentation.getWireframeVectors();
            List<Integer> edges = wireframeRepresentation.getEdges();

            double maxXValue = Double.MIN_VALUE;
            double maxYValue = Double.MIN_VALUE;
            for (FourCoordinatesVector point : points) {
                maxXValue = Math.max(maxXValue, point.getX());
                maxYValue = Math.max(maxYValue, point.getY());
            }

            for (int i = 0; i < edges.size(); i += 2) {
                FourCoordinatesVector p1 = points.get(edges.get(i));
                FourCoordinatesVector p2 = points.get(edges.get(i + 1));

                double normalizedX1 = p1.getX() / maxXValue;
                double normalizedY1 = p1.getY() / maxYValue;
                double normalizedX2 = p2.getX() / maxXValue;
//                double normalizedY2 = p2.getY() / maxYValue;
                Color colorEdge = interpolateColor(normalizedX1, normalizedY1, normalizedX2);
                graphics2D.setColor(colorEdge);
                graphics2D.drawLine(
                        (int) (centerX + p1.getX() * zoomParameter), (int) (centerY - p1.getY() * zoomParameter),
                        (int) (centerX + p2.getX() * zoomParameter), (int) (centerY - p2.getY() * zoomParameter)
                );
            }
        }
        drawXYZ(graphics2D, centerX, centerY);
    }

    private Color interpolateColor(double normalizedX1, double normalizedY1, double normalizedX2) {
        int red = Math.max(Math.min((int) (255 * normalizedX1), 255), 0);
        int green = Math.max(Math.min((int) (255 * normalizedY1), 255), 0);
        int blue = Math.max(Math.min((int) (255 * (1 - normalizedX2)), 255), 0);
        return new Color(red, green, blue);
    }

    private void drawXYZ(Graphics2D graphics2D, int centerX, int centerY) {
        double axisLength = 25;
        double zoomParameter = wireframeRepresentation.getZoomParameter();
        Matrix rotationMatrix = wireframeRepresentation.getRotationMatrix();

        FourCoordinatesVector xAxisStart = new FourCoordinatesVector(-axisLength, 0, 0);
        FourCoordinatesVector xAxisEnd = Matrix.multiplyMatrixByVector(rotationMatrix, xAxisStart);
        graphics2D.setColor(Color.BLUE);
        graphics2D.drawLine(centerX, centerY, centerX + (int) (xAxisEnd.getX() * zoomParameter), centerY - (int) (xAxisEnd.getY() * zoomParameter));

        FourCoordinatesVector yAxisStart = new FourCoordinatesVector(0, -axisLength, 0);
        FourCoordinatesVector yAxisEnd = Matrix.multiplyMatrixByVector(rotationMatrix, yAxisStart);
        graphics2D.setColor(Color.RED);
        graphics2D.drawLine(centerX, centerY, centerX + (int) (yAxisEnd.getX() * zoomParameter), centerY - (int) (yAxisEnd.getY() * zoomParameter));

        FourCoordinatesVector zAxisStart = new FourCoordinatesVector(0, 0, -axisLength);
        FourCoordinatesVector zAxisEnd = Matrix.multiplyMatrixByVector(rotationMatrix, zAxisStart);
        graphics2D.setColor(Color.GREEN);
        graphics2D.drawLine(centerX, centerY, centerX + (int) (zAxisEnd.getX() * zoomParameter), centerY - (int) (zAxisEnd.getY() * zoomParameter));
    }

    public void resetRotationMatrix() {
        wireframeRepresentation.resetRotationMatrix();
        lastPoint = new Point();
        repaint();
    }

    public String saveProgramStateAsJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        BSplineStateDto bSplineStateDto = new BSplineStateDto(
                bSplineRepresentation.getSupportPoints(),
                bSplineRepresentation.getBSplinePoints()
        );
        ApplicationParametersDto applicationParametersDto = new ApplicationParametersDto(
                applicationParameters.getNumberOfSupportPoints(),
                applicationParameters.getNumberOfBSplinePartSegments(),
                applicationParameters.getNumberOfFormingLines(),
                applicationParameters.getCircleSmoothingSegments(),
                applicationParameters.getZoomParameter()
        );
        double[][] rotationMatrix = wireframeRepresentation.getRotationMatrix().getElements();
        ProgramStateDto programStateDto = new ProgramStateDto(
                WIREFRAME_PROGRAM_ID,
                bSplineStateDto,
                applicationParametersDto,
                rotationMatrix
        );

        String serializerResult;
        try {
            serializerResult = objectMapper.writeValueAsString(programStateDto);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return serializerResult;
    }

    public void loadProgramStateFromJson(File jsonFile) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        ProgramStateDto programStateDto;
        try {
            programStateDto = objectMapper.readValue(jsonFile, ProgramStateDto.class);
        } catch (UnrecognizedPropertyException exception) {
            JOptionPane.showMessageDialog(this, INCORRECT_JSON_DESERIALIZATION, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        if (!WIREFRAME_PROGRAM_ID.equals(programStateDto.getProgramId())) {
            JOptionPane.showMessageDialog(this, INCORRECT_PROGRAM_ID, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
        BSplineStateDto bSplineStateDto = programStateDto.getBSplineStateDto();
        bSplineRepresentation.loadBSplineParameters(bSplineStateDto.getSupportPoints(), bSplineStateDto.getBSplinePoints());

        ApplicationParametersDto applicationParametersDto = programStateDto.getApplicationParametersDto();
        applicationParameters.loadApplicationParameters(
                applicationParametersDto.getNumberOfSupportPoints(),
                applicationParametersDto.getNumberOfBSplinePartSegments(),
                applicationParametersDto.getNumberOfFormingLines(),
                applicationParametersDto.getCircleSmoothingSegments(),
                applicationParametersDto.getZoomParameter()
        );

        double[][] rotationMatrixData = programStateDto.getRotationMatrix();
        Matrix rotationMatrix = new Matrix(rotationMatrixData);
        wireframeRepresentation.setRotationMatrix(rotationMatrix);
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
