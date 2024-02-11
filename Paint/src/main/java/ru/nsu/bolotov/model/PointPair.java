package ru.nsu.bolotov.model;

import java.awt.*;

public class PointPair {
    private final int x;
    private final int y;
    private final boolean isBorderPoint;
    private final Color selectedColor;

    public PointPair(int x, int y, boolean isBorderPoint, Color selectedColor) {
        this.x = x;
        this.y = y;
        this.isBorderPoint = isBorderPoint;
        this.selectedColor = selectedColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isBorderPoint() {
        return isBorderPoint;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }
}
