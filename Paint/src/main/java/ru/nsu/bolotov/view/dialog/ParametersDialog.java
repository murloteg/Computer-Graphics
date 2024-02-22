package ru.nsu.bolotov.view.dialog;

import ru.nsu.bolotov.model.polygon.PolygonParameters;
import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.swing.*;
import java.awt.*;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;

public class ParametersDialog extends JDialog {
    private final DrawablePanel drawablePanel;

    public ParametersDialog(JFrame owner, String title, DrawablePanel drawablePanel) {
        super(owner, title);
        this.drawablePanel = drawablePanel;

        JDialog lineParametersDialog = new JDialog();
        JDialog polygonParametersDialog = new JDialog();

        JButton lineButton = new JButton("Line Settings");
        JButton polygonButton = new JButton("Polygon Settings");

        JPanel chooseTypeDialogPanel = new JPanel(new GridLayout());
        chooseTypeDialogPanel.add(lineButton, BorderLayout.CENTER);
        chooseTypeDialogPanel.add(polygonButton, BorderLayout.CENTER);
        this.add(chooseTypeDialogPanel);

        polygonButton.addActionListener(event -> {
            polygonParametersDialog.setVisible(true);
            this.dispose();
        });

        lineButton.addActionListener(event -> {
            lineParametersDialog.setVisible(true);
            this.dispose();
        });

        prepareLineParametersDialog(lineParametersDialog);
        preparePolygonParametersDialog(polygonParametersDialog);

        this.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, CHOOSE_DIALOG_HEIGHT));
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void prepareLineParametersDialog(JDialog lineParametersDialog) {
        JComboBox<Integer> lineSizeComboBox = new JComboBox<>();
        for (int px = 1; px <= 30; ++px) {
            lineSizeComboBox.addItem(px);
        }

        JButton okLineButton = new JButton("Ok");
        okLineButton.setPreferredSize(new Dimension(60, 40));

        okLineButton.addActionListener(event -> {
            drawablePanel.setLineSize((Integer) lineSizeComboBox.getSelectedItem());
            lineParametersDialog.dispose();
        });

        JButton cancelLineButton = new JButton("Cancel");
        cancelLineButton.setPreferredSize(new Dimension(60, 40));

        cancelLineButton.addActionListener(event -> {
            lineParametersDialog.dispose();
        });

        JPanel lineSizeLabelPanel = new JPanel(new BorderLayout());
        JLabel lineSizeLabel = new JLabel("Select line size (px):");
        lineSizeLabelPanel.add(lineSizeLabel);

        JPanel lineSizeDialogPanel = new JPanel();
        lineSizeDialogPanel.add(lineSizeLabelPanel, BorderLayout.NORTH);
        lineSizeDialogPanel.add(lineSizeComboBox, BorderLayout.SOUTH);

        JPanel buttonsDialogPanel = new JPanel();
        buttonsDialogPanel.add(okLineButton, BorderLayout.SOUTH);
        buttonsDialogPanel.add(cancelLineButton, BorderLayout.SOUTH);

        lineParametersDialog.add(lineSizeDialogPanel, BorderLayout.NORTH);
        lineParametersDialog.add(buttonsDialogPanel, BorderLayout.SOUTH);

        lineParametersDialog.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, CHOOSE_DIALOG_HEIGHT));
        lineParametersDialog.pack();
        lineParametersDialog.setLocationRelativeTo(null);
    }

    private void preparePolygonParametersDialog(JDialog polygonParametersDialog) {
        PolygonParameters currentPolygonParameters = drawablePanel.getPolygonParameters();
        PolygonParameters updatedPolygonParameters = new PolygonParameters();
        PolygonParameters.copyParameters(currentPolygonParameters, updatedPolygonParameters);

        JPanel verticesPolygonParameters = createPanelWithPolygonVertices(updatedPolygonParameters);
        JPanel radiusPolygonParameters = createPanelWithPolygonRadius(updatedPolygonParameters);
        JPanel rotationPolygonParameters = createPanelWithPolygonRotation(updatedPolygonParameters);

        JPanel verticesPanel = new JPanel(new BorderLayout());
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        verticesPanel.add(verticesPolygonParameters, BorderLayout.NORTH);
        verticesPanel.add(separator);

        JPanel radiusAndRotationPanel = new JPanel(new BorderLayout());
        radiusAndRotationPanel.add(radiusPolygonParameters, BorderLayout.NORTH);
        radiusAndRotationPanel.add(rotationPolygonParameters, BorderLayout.SOUTH);

        JPanel unitedPanel = new JPanel(new BorderLayout());
        unitedPanel.add(verticesPanel, BorderLayout.NORTH);
        unitedPanel.add(radiusAndRotationPanel, BorderLayout.SOUTH);

        JButton okPolygonButton = new JButton("Ok");
        okPolygonButton.setPreferredSize(new Dimension(60, 40));

        okPolygonButton.addActionListener(event -> {
            PolygonParameters.copyParameters(updatedPolygonParameters, currentPolygonParameters);
            polygonParametersDialog.dispose();
        });

        JButton cancelPolygonButton = new JButton("Cancel");
        cancelPolygonButton.setPreferredSize(new Dimension(60, 40));

        cancelPolygonButton.addActionListener(event -> {
            polygonParametersDialog.dispose();
        });

        JPanel buttonsDialogPanel = new JPanel();
        buttonsDialogPanel.add(okPolygonButton, BorderLayout.SOUTH);
        buttonsDialogPanel.add(cancelPolygonButton, BorderLayout.SOUTH);

        polygonParametersDialog.add(unitedPanel, BorderLayout.NORTH);
        polygonParametersDialog.add(buttonsDialogPanel, BorderLayout.SOUTH);


        polygonParametersDialog.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, POLYGON_DIALOG_HEIGHT));
        polygonParametersDialog.pack();
        polygonParametersDialog.setLocationRelativeTo(null);
    }

    private JPanel createPanelWithPolygonVertices(PolygonParameters updatedPolygonParameters) {
        JPanel numberOfVerticesPanel = new JPanel();
        JLabel numberOfVerticesLabel = new JLabel("Number of vertices:");
        numberOfVerticesPanel.add(numberOfVerticesLabel);

        JComboBox<Integer> verticesComboBox = new JComboBox<>();
        for (int i = 3; i <= 13; ++i) {
            verticesComboBox.addItem(i);
        }
        verticesComboBox.setSelectedItem(updatedPolygonParameters.getNumberOfVertices());

        verticesComboBox.addActionListener(event -> {
            updatedPolygonParameters.setNumberOfVertices((Integer) verticesComboBox.getSelectedItem());
        });

        JPanel upperPolygonPanel = new JPanel();
        upperPolygonPanel.add(numberOfVerticesPanel, BorderLayout.NORTH);
        upperPolygonPanel.add(verticesComboBox, BorderLayout.SOUTH);
        return upperPolygonPanel;
    }

    private JPanel createPanelWithPolygonRadius(PolygonParameters updatedPolygonParameters) {
        JPanel radiusLabelPanel = new JPanel();
        JLabel radiusLabel = new JLabel("Select radius (px):");
        radiusLabelPanel.add(radiusLabel);

        JComboBox<Integer> radiusComboBox = new JComboBox<>();
        for (int i = 5; i <= 200; i += 5) {
            radiusComboBox.addItem(i);
        }
        radiusComboBox.setSelectedItem(updatedPolygonParameters.getRadiusInPx());

        JSlider radiusSlider = new JSlider(5, 200);
        radiusSlider.setValue(updatedPolygonParameters.getRadiusInPx());

        radiusSlider.addChangeListener(event -> {
            radiusComboBox.setSelectedItem(radiusSlider.getValue());
            updatedPolygonParameters.setRadiusInPx(radiusSlider.getValue());
        });

        radiusComboBox.addActionListener(event -> {
            radiusSlider.setValue((Integer) radiusComboBox.getSelectedItem());
            updatedPolygonParameters.setRadiusInPx(radiusSlider.getValue());
        });

        JPanel radiusPanel = new JPanel();
        radiusPanel.add(radiusSlider, BorderLayout.CENTER);
        radiusPanel.add(radiusComboBox, BorderLayout.SOUTH);

        JPanel upperPolygonPanel = new JPanel(new BorderLayout());
        upperPolygonPanel.add(radiusLabelPanel, BorderLayout.NORTH);
        upperPolygonPanel.add(radiusPanel, BorderLayout.SOUTH);
        return upperPolygonPanel;
    }

    private JPanel createPanelWithPolygonRotation(PolygonParameters updatedPolygonParameters) {
        JPanel rotationLabelPanel = new JPanel();
        JLabel rotationLabel = new JLabel("Select rotation in degrees:");
        rotationLabelPanel.add(rotationLabel);

        JComboBox<Integer> rotationComboBox = new JComboBox<>();
        for (int i = 0; i <= 360; ++i) {
            rotationComboBox.addItem(i);
        }
        rotationComboBox.setSelectedItem(updatedPolygonParameters.getRotationInDegrees());

        JSlider rotationSlider = new JSlider(0, 360);
        rotationSlider.setValue(updatedPolygonParameters.getRotationInDegrees());

        rotationSlider.addChangeListener(event -> {
            rotationComboBox.setSelectedItem(rotationSlider.getValue());
            updatedPolygonParameters.setRotationInDegrees(rotationSlider.getValue());
        });

        rotationComboBox.addActionListener(event -> {
            rotationSlider.setValue((Integer) rotationComboBox.getSelectedItem());
            updatedPolygonParameters.setRotationInDegrees(rotationSlider.getValue());
        });

        JPanel rotationPanel = new JPanel();
        rotationPanel.add(rotationSlider, BorderLayout.NORTH);
        rotationPanel.add(rotationComboBox, BorderLayout.SOUTH);

        JPanel lowerPolygonPanel = new JPanel(new BorderLayout());
        lowerPolygonPanel.add(rotationLabelPanel, BorderLayout.NORTH);
        lowerPolygonPanel.add(rotationPanel, BorderLayout.SOUTH);
        return lowerPolygonPanel;
    }
}
