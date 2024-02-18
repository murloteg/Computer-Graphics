package ru.nsu.bolotov.model.data;

import java.util.Objects;

public class SpanCoords implements Comparable<SpanCoords> {
    private int leftBorderX;
    private int rightBorderX;
    private int coordY;

    public SpanCoords(int leftBorderX, int rightBorderX, int coordY) {
        this.leftBorderX = leftBorderX;
        this.rightBorderX = rightBorderX;
        this.coordY = coordY;
    }

    public int getLeftBorderX() {
        return leftBorderX;
    }

    public void setLeftBorderX(int leftBorderX) {
        this.leftBorderX = leftBorderX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public int getRightBorderX() {
        return rightBorderX;
    }

    public void setRightBorderX(int rightBorderX) {
        this.rightBorderX = rightBorderX;
    }

    @Override
    public int compareTo(SpanCoords other) {
        return Integer.compare(this.leftBorderX, other.leftBorderX) + Integer.compare(this.rightBorderX, other.rightBorderX) +
                Integer.compare(this.coordY, other.coordY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpanCoords that = (SpanCoords) o;
        return leftBorderX == that.leftBorderX && rightBorderX == that.rightBorderX && coordY == that.coordY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftBorderX, rightBorderX, coordY);
    }
}
