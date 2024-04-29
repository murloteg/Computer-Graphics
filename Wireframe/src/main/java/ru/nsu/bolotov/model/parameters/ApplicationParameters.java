package ru.nsu.bolotov.model.parameters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.*;

public class ApplicationParameters {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private int numberOfSupportPoints;
    private int numberOfBSplinePartSegments;
    private int numberOfFormingLines;
    private int circleSmoothingSegments;

    public ApplicationParameters() {
        this.numberOfSupportPoints = 0;
        this.numberOfBSplinePartSegments = DEFAULT_NUMBER_OF_BSPLINE_PART_SEGMENTS;
        this.numberOfFormingLines = DEFAULT_NUMBER_OF_FORMING_LINES;
        this.circleSmoothingSegments = DEFAULT_CIRCLE_SMOOTHING_SEGMENTS;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public int getNumberOfSupportPoints() {
        return numberOfSupportPoints;
    }

    public void setNumberOfSupportPoints(int numberOfSupportPoints) {
        this.numberOfSupportPoints = numberOfSupportPoints;
        propertyChangeSupport.firePropertyChange("updateSupportPoints", null, this);
    }

    public int getNumberOfBSplinePartSegments() {
        return numberOfBSplinePartSegments;
    }

    public void setNumberOfBSplinePartSegments(int numberOfBSplinePartSegments) {
        this.numberOfBSplinePartSegments = numberOfBSplinePartSegments;
    }

    public int getNumberOfFormingLines() {
        return numberOfFormingLines;
    }

    public void setNumberOfFormingLines(int numberOfFormingLines) {
        this.numberOfFormingLines = numberOfFormingLines;
    }

    public int getCircleSmoothingSegments() {
        return circleSmoothingSegments;
    }

    public void setCircleSmoothingSegments(int circleSmoothingSegments) {
        this.circleSmoothingSegments = circleSmoothingSegments;
    }
}
