package ru.nsu.bolotov.model;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.*;

public class WireframeRepresentation {
    private final ApplicationParameters applicationParameters;
    private Matrix cameraPerspectiveProjectionMatrix;
    private Matrix normalizeMatrix;

    @Getter
    @Setter
    private Matrix rotationMatrix;

    @Getter
    private final List<FourCoordinatesVector> wireframeVectors = new ArrayList<>();

    @Getter
    private final List<Integer> edges = new ArrayList<>();

    public WireframeRepresentation(ApplicationParameters applicationParameters) {
        this.applicationParameters = applicationParameters;
        initializeRotationMatrix();
        initializePerspectiveProjectionMatrix();
    }

    public double getZoomParameter() {
        return applicationParameters.getZoomParameter();
    }

    public void updateRotationMatrix(Point first, Point second) {
        FourCoordinatesVector axis = new FourCoordinatesVector(-(second.getY() - first.getY()), -(second.getX() - first.getX()), 0);
        if (axis.getX() == 0 && axis.getY() == 0) {
            return;
        }
        axis.normalization();

        Matrix rotation = findRotationMatrix(axis);
        rotationMatrix = Matrix.multiplyMatrixByMatrix(rotation, rotationMatrix);
    }

    private Matrix findRotationMatrix(FourCoordinatesVector axis) {
        double cos = Math.cos(Math.toRadians(DEFAULT_ROTATION_ANGLE_DEGREES));
        double sin = Math.sin(Math.toRadians(DEFAULT_ROTATION_ANGLE_DEGREES));
        double x = axis.getX();
        double y = axis.getY();
        double z = axis.getZ();
        return new Matrix(new double[][]{
                {cos + (1 - cos) * x * x, (1 - cos) * x * y - sin * z, (1 - cos) * x * z + sin * y, 0.0},
                {(1 - cos) * y * x + sin * z, cos + (1 - cos) * y * y, (1 - cos) * y * z - sin * x, 0.0},
                {(1 - cos) * z * x - sin * y, (1 - cos) * z * y + sin * x, cos + (1 - cos) * z * z, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

    public void resetRotationMatrix() {
        initializeRotationMatrix();
    }

    public void updateZoomParameter(double zoomParameter) {
        applicationParameters.setZoomParameter(zoomParameter);
    }

    private void initializeRotationMatrix() {
        this.rotationMatrix = new Matrix(new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

    private void initializePerspectiveProjectionMatrix() {
        this.cameraPerspectiveProjectionMatrix = new Matrix(new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 0.0, DEFAULT_PERSPECTIVE_PROJECTION_SCALE},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

    private void setNormalizeMatrix() {
        FourCoordinatesVector firstWireframeVector = wireframeVectors.get(0);
        double maxX = firstWireframeVector.getX();
        double minX = firstWireframeVector.getX();
        double maxY = firstWireframeVector.getY();
        double minY = firstWireframeVector.getY();
        double maxZ = firstWireframeVector.getZ();
        double minZ = firstWireframeVector.getZ();

        for (FourCoordinatesVector vector : wireframeVectors) {
            if (vector.getX() < minX) {
                minX = vector.getX();
            }
            if (vector.getX() > maxX) {
                maxX = vector.getX();
            }
            if (vector.getY() < minY) {
                minY = vector.getY();
            }
            if (vector.getY() > maxY) {
                maxY = vector.getY();
            }
            if (vector.getZ() < minZ) {
                minZ = vector.getZ();
            }
            if (vector.getZ() > maxZ) {
                maxZ = vector.getZ();
            }
        }
        double distanceX = maxX - minX;
        double distanceY = maxY - minY;
        double distanceZ = maxZ - minZ;
        double maxDistance = Math.max(distanceX, Math.max(distanceY, distanceZ));
        if (maxDistance == 0.0) {
            maxDistance = 1.0;
        }

        normalizeMatrix = new Matrix(new double[][]{
                {1.0 / maxDistance, 0.0, 0.0, 0.0},
                {0.0, 1.0 / maxDistance, 0.0, 0.0},
                {0.0, 0.0, 1.0 / maxDistance, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        });
    }

    public void createWireframePoints(List<Point2D> bSplinePoints) {
        wireframeVectors.clear();
        edges.clear();
        int numberOfFormingLines = applicationParameters.getNumberOfFormingLines();
        int numberOfCircles = applicationParameters.getNumberOfSupportPoints() - 2;
        int circleSmoothingSegments = applicationParameters.getCircleSmoothingSegments();

        double angle = (double) 360 / (numberOfFormingLines * circleSmoothingSegments);
        double sumX = 0.0;
        for (Point2D point : bSplinePoints) {
            sumX += point.getX();
        }
        double centerX = sumX / bSplinePoints.size();

        Matrix resultMatrix = Matrix.multiplyMatrixByMatrix(cameraPerspectiveProjectionMatrix, rotationMatrix);
        for (int i = 0; i < numberOfFormingLines * circleSmoothingSegments; i++) {
            double angleForPart = i * angle;
            double cos = Math.cos(Math.toRadians(angleForPart));
            double sin = Math.sin(Math.toRadians(angleForPart));
            for (Point2D p : bSplinePoints) {
                FourCoordinatesVector newPoint = new FourCoordinatesVector(p.getY() * cos, p.getY() * sin, p.getX() - centerX);
                newPoint = Matrix.multiplyMatrixByVector(resultMatrix, newPoint);
                wireframeVectors.add(newPoint);
            }
        }
        setNormalizeMatrix();
        wireframeVectors.replaceAll(point -> Matrix.multiplyMatrixByVector(normalizeMatrix, point));

        int N = bSplinePoints.size();
        for (int i = 0; i < numberOfFormingLines * circleSmoothingSegments; i += circleSmoothingSegments) {
            for (int j = 0; j < N - 1; j++) {
                edges.add(j + i * N);
                edges.add(j + 1 + i * N);
            }
        }

        for (int i = 0; i < numberOfFormingLines * circleSmoothingSegments; i++) {
            edges.add(i * N);
            edges.add(((i + 1) % (numberOfFormingLines * circleSmoothingSegments)) * N);
        }

        for (int i = 0; i < numberOfFormingLines * circleSmoothingSegments; i++) {
            edges.add(i * N + N - 1);
            edges.add(((i + 1) % (numberOfFormingLines * circleSmoothingSegments)) * N + N - 1);
        }

        if (numberOfCircles > 2) {
            int step = N / (numberOfCircles - 1);
            for (int i = 1; i <= numberOfCircles - 2; i++) {
                for (int j = 0; j < numberOfFormingLines * circleSmoothingSegments; j++) {
                    edges.add(j * N + i * step);
                    edges.add(((j + 1) % (numberOfFormingLines * circleSmoothingSegments)) * N + i * step);
                }
            }
        }
    }

    public Map<Integer, List<Point2D>> defaultWireframeExample() {
        List<FourCoordinatesVector> examplePoints = new ArrayList<>();
        examplePoints.add(new FourCoordinatesVector(1.0, 0.0, 0.0));
        examplePoints.add(new FourCoordinatesVector(0.0, 1.0, 0.0));
        examplePoints.add(new FourCoordinatesVector(-1.0, 0.0, 0.0));
        examplePoints.add(new FourCoordinatesVector(0.0, -1.0, 0.0));
        examplePoints.add(new FourCoordinatesVector(0.0, 0.0, 1.0));
        examplePoints.add(new FourCoordinatesVector(0.0, 0.0, -1.0));
        examplePoints.add(new FourCoordinatesVector(1.0, 0.0, 0.0));
        examplePoints.add(new FourCoordinatesVector(0.0, 1.0, 0.0));

        List<Point2D> exampleVectors = new ArrayList<>();
        Matrix resultMatrix = Matrix.multiplyMatrixByMatrix(cameraPerspectiveProjectionMatrix, rotationMatrix);
        for (FourCoordinatesVector p : examplePoints) {
            FourCoordinatesVector planePoint;
            planePoint = Matrix.multiplyMatrixByVector(resultMatrix, p);
            exampleVectors.add(new Point2D.Double(planePoint.getX(), planePoint.getY()));
        }

        List<Point2D> exampleEdges = new ArrayList<>();
        exampleEdges.add(new Point2D.Double(0, 1));
        exampleEdges.add(new Point2D.Double(1, 2));
        exampleEdges.add(new Point2D.Double(2, 3));
        exampleEdges.add(new Point2D.Double(3, 0));
        exampleEdges.add(new Point2D.Double(0, 4));
        exampleEdges.add(new Point2D.Double(1, 4));
        exampleEdges.add(new Point2D.Double(2, 4));
        exampleEdges.add(new Point2D.Double(3, 4));
        exampleEdges.add(new Point2D.Double(0, 5));
        exampleEdges.add(new Point2D.Double(1, 5));
        exampleEdges.add(new Point2D.Double(2, 5));
        exampleEdges.add(new Point2D.Double(3, 5));

        Map<Integer, List<Point2D>> wireframeExample = new HashMap<>();
        wireframeExample.put(0, exampleVectors);
        wireframeExample.put(1, exampleEdges);
        return wireframeExample;
    }
}
