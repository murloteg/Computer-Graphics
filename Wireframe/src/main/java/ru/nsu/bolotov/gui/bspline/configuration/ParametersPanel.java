package ru.nsu.bolotov.gui.bspline.configuration;

import ru.nsu.bolotov.gui.bspline.EditorPanel;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ParametersPanel extends JPanel implements PropertyChangeListener {
    private final ApplicationParameters applicationParameters;
    private final JComboBox<Integer> formingLinesComboBox = new JComboBox<>();
    private final JComboBox<Integer> circleSegmentsComboBox = new JComboBox<>();
    private final JComboBox<Integer> bSplineSegmentsComboBox = new JComboBox<>();
    private final JLabel supportPointsCounter;

    public ParametersPanel(ApplicationParameters applicationParameters, EditorPanel editorPanel) {
        this.applicationParameters = applicationParameters;
        this.applicationParameters.addPropertyChangeListener(this);
        supportPointsCounter = new JLabel("" + applicationParameters.getNumberOfSupportPoints());
        initializeParametersPanel(editorPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("updateSupportPoints".equals(evt.getPropertyName())) {
            supportPointsCounter.setText("" + applicationParameters.getNumberOfSupportPoints());
        } else if ("loadParameters".equals(evt.getPropertyName())) {
            formingLinesComboBox.setSelectedItem(applicationParameters.getNumberOfFormingLines());
            circleSegmentsComboBox.setSelectedItem(applicationParameters.getCircleSmoothingSegments());
            bSplineSegmentsComboBox.setSelectedItem(applicationParameters.getNumberOfBSplinePartSegments());
            supportPointsCounter.setText("" + applicationParameters.getNumberOfSupportPoints());
        }
    }

    private void initializeParametersPanel(EditorPanel editorPanel) {
        JLabel formingLinesLabel = new JLabel("Forming Lines");
        this.add(formingLinesLabel);

        for (int i = 2; i <= 15; ++i) {
            formingLinesComboBox.addItem(i);
        }
        formingLinesComboBox.setSelectedItem(applicationParameters.getNumberOfFormingLines());
        formingLinesComboBox.addItemListener(event -> {
            applicationParameters.setNumberOfFormingLines((int) formingLinesComboBox.getSelectedItem());
            editorPanel.updateBSpline();
        });
        this.add(formingLinesComboBox);

        JLabel circleSegmentsLabel = new JLabel("Circle Segments");
        this.add(circleSegmentsLabel);

        for (int i = 1; i <= 15; ++i) {
            circleSegmentsComboBox.addItem(i);
        }
        circleSegmentsComboBox.setSelectedItem(applicationParameters.getCircleSmoothingSegments());
        circleSegmentsComboBox.addItemListener(event -> {
            applicationParameters.setCircleSmoothingSegments((int) circleSegmentsComboBox.getSelectedItem());
            editorPanel.updateBSpline();
        });
        this.add(circleSegmentsComboBox);

        // TODO: check that numberOfBSplinePartSegments == 1 it's OK for 4-points bSpline
        JLabel bSplineSegmentsLabel = new JLabel("BSpline Part Segments");
        this.add(bSplineSegmentsLabel);
        for (int i = 1; i <= 20; ++i) {
            bSplineSegmentsComboBox.addItem(i);
        }
        bSplineSegmentsComboBox.setSelectedItem(applicationParameters.getNumberOfBSplinePartSegments());
        bSplineSegmentsComboBox.addItemListener(event -> {
            applicationParameters.setNumberOfBSplinePartSegments((int) bSplineSegmentsComboBox.getSelectedItem());
            editorPanel.updateBSpline();
        });
        this.add(bSplineSegmentsComboBox);

        JLabel supportPointsNumberLabel = new JLabel("Support points: ");
        this.add(supportPointsNumberLabel);
        this.add(supportPointsCounter);
    }
}
