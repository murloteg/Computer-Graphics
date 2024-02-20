package ru.nsu.bolotov.view.dialog;

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

//        this.add(lineButton);
//        this.add(polygonButton);

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


        // Polygon:
        // Radius
        JPanel radiusLabelPanel = new JPanel();
        JLabel radiusLabel = new JLabel("Select radius (px):");
        radiusLabelPanel.add(radiusLabel);

        JComboBox<Integer> radiusComboBox = new JComboBox<>();
        for (int i = 0; i <= 20; ++i) {
            radiusComboBox.addItem(i);
        }

        JSlider radiusSlider = new JSlider(0, 20);

        radiusSlider.addChangeListener(event -> {
            radiusComboBox.setSelectedItem(radiusSlider.getValue());
        });

        radiusComboBox.addActionListener(event -> {
            radiusSlider.setValue((Integer) radiusComboBox.getSelectedItem());
        });

        JPanel radiusPanel = new JPanel();
//        radiusPanel.add(radiusLabel, BorderLayout.NORTH);
        radiusPanel.add(radiusSlider, BorderLayout.CENTER);
        radiusPanel.add(radiusComboBox, BorderLayout.SOUTH);


        // Rotation
        JPanel rotationLabelPanel = new JPanel();
        JLabel rotationLabel = new JLabel("Select rotation in degrees:");
        rotationLabelPanel.add(rotationLabel);

        JComboBox<Integer> rotationComboBox = new JComboBox<>();
        for (int i = 0; i <= 360; ++i) {
            rotationComboBox.addItem(i);
        }

        JSlider rotationSlider = new JSlider(0, 360);

        rotationSlider.addChangeListener(event -> {
            rotationComboBox.setSelectedItem(rotationSlider.getValue());
        });

        rotationComboBox.addActionListener(event -> {
            rotationSlider.setValue((Integer) rotationComboBox.getSelectedItem());
        });

        JPanel rotationPanel = new JPanel();
        rotationPanel.add(rotationSlider, BorderLayout.NORTH);
        rotationPanel.add(rotationComboBox, BorderLayout.SOUTH);

        JPanel upperPolygonPanel = new JPanel(new BorderLayout());
        upperPolygonPanel.add(radiusLabelPanel, BorderLayout.NORTH);
        upperPolygonPanel.add(radiusPanel, BorderLayout.SOUTH);

        JPanel lowerPolygonPanel = new JPanel(new BorderLayout());
        lowerPolygonPanel.add(rotationLabelPanel, BorderLayout.NORTH);
        lowerPolygonPanel.add(rotationPanel, BorderLayout.SOUTH);

//        polygonParametersDialog.add(formPanel, BorderLayout.NORTH); //

        polygonParametersDialog.add(upperPolygonPanel, BorderLayout.NORTH);
        polygonParametersDialog.add(lowerPolygonPanel, BorderLayout.SOUTH);

        polygonParametersDialog.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, POLYGON_DIALOG_HEIGHT));
        polygonParametersDialog.pack();
        polygonParametersDialog.setLocationRelativeTo(null);

        this.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, CHOOSE_DIALOG_HEIGHT));
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
