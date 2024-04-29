package ru.nsu.bolotov.controller;

import ru.nsu.bolotov.gui.bspline.BSplineEditorFrame;
import ru.nsu.bolotov.gui.wireframe.WireframeWindow;
import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

public class ApplicationController {
    public static void main(String[] args) {
        BSplineRepresentation bSplineRepresentation = new BSplineRepresentation();
        ApplicationParameters applicationParameters = new ApplicationParameters();
        BSplineEditorFrame bSplineEditorFrame = new BSplineEditorFrame(bSplineRepresentation, applicationParameters);
        WireframeWindow wireframeWindow = new WireframeWindow(bSplineRepresentation, applicationParameters);
    }
}
