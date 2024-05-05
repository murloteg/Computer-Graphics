package ru.nsu.bolotov.model.parameters;

import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static ru.nsu.bolotov.util.UtilConsts.DefaultApplicationParameters.*;

public class ApplicationParameters {
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Getter
    private int numberOfSupportPoints;

    @Getter
    @Setter
    private int numberOfBSplinePartSegments;

    @Getter
    @Setter
    private int numberOfFormingLines;

    @Getter
    @Setter
    private int circleSmoothingSegments;

    @Getter
    @Setter
    private double zoomParameter;

    public ApplicationParameters() {
        this.numberOfSupportPoints = 0;
        this.numberOfBSplinePartSegments = DEFAULT_NUMBER_OF_BSPLINE_PART_SEGMENTS;
        this.numberOfFormingLines = DEFAULT_NUMBER_OF_FORMING_LINES;
        this.circleSmoothingSegments = DEFAULT_CIRCLE_SMOOTHING_SEGMENTS;
        this.zoomParameter = DEFAULT_WIREFRAME_ZOOM_PARAMETER;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void setNumberOfSupportPoints(int numberOfSupportPoints) {
        this.numberOfSupportPoints = numberOfSupportPoints;
        propertyChangeSupport.firePropertyChange("updateSupportPoints", null, this);
    }

    public void loadApplicationParameters(
            int numberOfSupportPoints,
            int numberOfBSplinePartSegments,
            int numberOfFormingLines,
            int circleSmoothingSegments,
            double zoomParameter
    ) {
        this.numberOfSupportPoints = numberOfSupportPoints;
        this.numberOfBSplinePartSegments = numberOfBSplinePartSegments;
        this.numberOfFormingLines = numberOfFormingLines;
        this.circleSmoothingSegments = circleSmoothingSegments;
        this.zoomParameter = zoomParameter;
        propertyChangeSupport.firePropertyChange("loadParameters", null, this);
    }
}
