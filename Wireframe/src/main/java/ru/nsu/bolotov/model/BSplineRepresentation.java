package ru.nsu.bolotov.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.DEFAULT_NUMBER_OF_BSPLINE_PART_SEGMENTS;

public class BSplineRepresentation {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Matrix baseMatrix = new Matrix(
            new double[][] {
                    {-1.0 / 6, 1.0 / 2, -1.0 / 2, 1.0 / 6},
                    {1.0 / 2, -1.0, 1.0 / 2, 0},
                    {-1.0 / 2, 0, 1.0 / 2, 0},
                    {1.0 / 6, 2.0 / 3, 1.0 / 6, 0}
            }
    );

    @Getter
    private List<Point2D> supportPoints = new ArrayList<>();

    @Getter
    private List<Point2D> bSplinePoints = new ArrayList<>();

    @Setter
    private int numberOfBSplinePartSegments = DEFAULT_NUMBER_OF_BSPLINE_PART_SEGMENTS;

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void loadBSplineParameters(List<Point2D> supportPoints, List<Point2D> bSplinePoints) {
        this.supportPoints = supportPoints;
        this.bSplinePoints = bSplinePoints;
        propertyChangeSupport.firePropertyChange("bSplineUpdated", null, this);
    }

    public void addNewSupportPoint(Point2D point2D) {
        supportPoints.add(point2D);
        createBSpline();
    }

    public void deleteSupportPointByIndex(int index) {
        supportPoints.remove(index);
        createBSpline();
    }

    public void createBSpline() {
        bSplinePoints.clear();
        double tStep = 1.0 / numberOfBSplinePartSegments;
        int supportPointsCount = supportPoints.size();
        
        if (supportPointsCount < 4) {
            return;
        }
        
        for (int i = 1; i < supportPointsCount - 2; ++i) {
            double[] xCoords = {
                    supportPoints.get(i-1).getX(),
                    supportPoints.get(i).getX(),
                    supportPoints.get(i+1).getX(),
                    supportPoints.get(i+2).getX()
            };

            double[] yCoords = {
                    supportPoints.get(i-1).getY(),
                    supportPoints.get(i).getY(),
                    supportPoints.get(i+1).getY(),
                    supportPoints.get(i+2).getY()
            };

            double[] xVector = Matrix.multiplyMatrixByVector(baseMatrix, xCoords);
            double[] yVector = Matrix.multiplyMatrixByVector(baseMatrix, yCoords);

            double currentT = 0;
            for (int j = 0; j <= numberOfBSplinePartSegments; ++j) {
                currentT = j * tStep;
                double calculatedX = currentT * currentT * currentT * xVector[0] + currentT * currentT * xVector[1] + currentT * xVector[2] + xVector[3];
                double calculatedY = currentT * currentT * currentT * yVector[0] + currentT * currentT * yVector[1] + currentT * yVector[2] + yVector[3];
                bSplinePoints.add(new Point2D.Double(calculatedX, calculatedY));
            }
        }
        propertyChangeSupport.firePropertyChange("bSplineUpdated", null, this);
    }
}
