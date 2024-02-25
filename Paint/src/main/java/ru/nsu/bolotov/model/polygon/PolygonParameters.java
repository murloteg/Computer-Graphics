package ru.nsu.bolotov.model.polygon;

import static ru.nsu.bolotov.util.UtilConsts.PolygonConsts.DEFAULT_POLYGON_VERTICES;
import static ru.nsu.bolotov.util.UtilConsts.PolygonConsts.DEFAULT_POLYGON_RADIUS;
import static ru.nsu.bolotov.util.UtilConsts.PolygonConsts.DEFAULT_POLYGON_ROTATION;

public class PolygonParameters {
    private PolygonForm polygonForm;
    private int rotationInDegrees;
    private int radiusInPx;
    private int numberOfVertices;

    public PolygonParameters() {
        this.polygonForm = PolygonForm.CONVEX;
        this.numberOfVertices = DEFAULT_POLYGON_VERTICES;
        this.radiusInPx = DEFAULT_POLYGON_RADIUS;
        this.rotationInDegrees = DEFAULT_POLYGON_ROTATION;
    }

    public PolygonForm getPolygonForm() {
        return polygonForm;
    }

    public void setPolygonForm(PolygonForm polygonForm) {
        this.polygonForm = polygonForm;
    }

    public int getRotationInDegrees() {
        return rotationInDegrees;
    }

    public void setRotationInDegrees(int rotationInDegrees) {
        this.rotationInDegrees = rotationInDegrees;
    }

    public int getRadiusInPx() {
        return radiusInPx;
    }

    public void setRadiusInPx(int radiusInPx) {
        this.radiusInPx = radiusInPx;
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public void setNumberOfVertices(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
    }

    public static void copyParameters(PolygonParameters source, PolygonParameters destination) {
        destination.setPolygonForm(source.getPolygonForm());
        destination.setNumberOfVertices(source.getNumberOfVertices());
        destination.setRadiusInPx(source.getRadiusInPx());
        destination.setRotationInDegrees(source.getRotationInDegrees());
    }
}
