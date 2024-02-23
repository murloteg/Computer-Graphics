package ru.nsu.bolotov.model.polygon;

public class PolygonParameters {
    private PolygonForm polygonForm;
    private int rotationInDegrees;
    private int radiusInPx;
    private int numberOfVertices;

    public PolygonParameters() {
        this.polygonForm = PolygonForm.CONVEX;
        this.numberOfVertices = 3; // TODO
        this.radiusInPx = 45;
        this.rotationInDegrees = 0;
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
