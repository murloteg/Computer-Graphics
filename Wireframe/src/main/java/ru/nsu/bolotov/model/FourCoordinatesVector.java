package ru.nsu.bolotov.model;

public class FourCoordinatesVector {
    private double x;
    private double y;
    private double z;
    private double additional;

    public FourCoordinatesVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.additional = 1.0;
    }

    public void normalization() {
        double totalLength = Math.sqrt(x * x + y * y + z * z);
        x /= totalLength;
        y /= totalLength;
        z /= totalLength;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setAdditional(double additional) {
        this.additional = additional;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getAdditional() {
        return additional;
    }
}
