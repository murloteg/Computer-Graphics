package ru.nsu.bolotov.model.polygon;

public class PolygonParameters {
    private PolygonForm polygonForm;
    private int rotationInDegrees;
    private int radiusInPx;
    private int numberOfVertices;

    public PolygonParameters(PolygonForm polygonForm, int rotationInDegrees, int radiusInPx) {
        this.polygonForm = polygonForm;
        this.rotationInDegrees = rotationInDegrees;
        this.radiusInPx = radiusInPx;
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
}
