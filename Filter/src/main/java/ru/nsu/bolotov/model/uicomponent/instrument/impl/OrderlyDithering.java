package ru.nsu.bolotov.model.uicomponent.instrument.impl;

import ru.nsu.bolotov.model.uicomponent.instrument.DialogEnabled;
import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.model.filter.mode.FilterMode;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.CHOOSE_DIALOG_HEIGHT;

public class OrderlyDithering implements Instrument, DialogEnabled {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;
    private final List<?> defaultInstrumentParameters = List.of(2, 2, 2);

    public OrderlyDithering(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        String instrumentName = this.getInstrumentName();

        this.instrumentButton = new JButton();
        this.instrumentButton.setToolTipText(instrumentName);
        this.menuButton = new JRadioButtonMenuItem(instrumentName);
    }

    @Override
    public JButton getInstrumentButton() {
        return instrumentButton;
    }

    @Override
    public JRadioButtonMenuItem getMenuButton() {
        return menuButton;
    }

    @Override
    public String getInstrumentName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void injectActionListeners(List<Instrument> instrumentList, ButtonGroup buttonGroup) {
        buttonGroup.add(menuButton);
        instrumentButton.addActionListener(event -> {
            showDialog();
            imagePanel.setFilterMode(FilterMode.ORDERLY_DITHERING);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            menuButton.setSelected(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });

        menuButton.addActionListener(event -> {
            showDialog();
            imagePanel.setFilterMode(FilterMode.ORDERLY_DITHERING);
            menuButton.setSelected(true);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });
    }

    @Override
    public void changeSelectState() {
        instrumentButton.setSelected(false);
    }

    @Override
    public List<?> getDefaultParameters() {
        return defaultInstrumentParameters;
    }

    @Override
    public void showDialog() {
        JDialog parametersDialog = new JDialog();
        JTextField redQuantizationField = new JTextField();
        JTextField greenQuantizationField = new JTextField();
        JTextField blueQuantizationField = new JTextField();

        List<?> parameters = imagePanel.getPreviousParametersForInstrument(getInstrumentName());
        String redQuantization = String.valueOf(Objects.requireNonNull(parameters.get(0)));
        String greenQuantization = String.valueOf(Objects.requireNonNull(parameters.get(1)));
        String blueQuantization = String.valueOf(Objects.requireNonNull(parameters.get(2)));
        redQuantizationField.setText(redQuantization);
        greenQuantizationField.setText(greenQuantization);
        blueQuantizationField.setText(blueQuantization);

        JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(CONFIRM_BUTTON_WIDTH, STANDARD_BUTTON_SIZE));

        okButton.addActionListener(event -> {
            String currentRedQuantization = redQuantizationField.getText();
            String currentGreenQuantization = greenQuantizationField.getText();
            String currentBlueQuantization = blueQuantizationField.getText();
            if (checkIfInputIsValid(currentRedQuantization, parametersDialog) &&
                    checkIfInputIsValid(currentGreenQuantization, parametersDialog) &&
                    checkIfInputIsValid(currentBlueQuantization, parametersDialog)) {
                int redQuantizationValue = Integer.parseInt(currentRedQuantization);
                int greenQuantizationValue = Integer.parseInt(currentGreenQuantization);
                int blueQuantizationValue = Integer.parseInt(currentBlueQuantization);
                List<?> parametersList = List.of(redQuantizationValue, greenQuantizationValue, blueQuantizationValue);
                imagePanel.addStateToApplicationParameters(getInstrumentName(), parametersList);
            }
            parametersDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(CONFIRM_BUTTON_WIDTH, STANDARD_BUTTON_SIZE));

        cancelButton.addActionListener(event -> {
            parametersDialog.dispose();
        });

        JPanel redLabelPanel = new JPanel();
        JLabel redLabel = new JLabel("Choose red quantization:");
        redLabelPanel.add(redLabel);

        JPanel redPanel = new JPanel(new BorderLayout());
        redPanel.add(redLabelPanel, BorderLayout.NORTH);
        redPanel.add(redQuantizationField, BorderLayout.SOUTH);

        JPanel greenLabelPanel = new JPanel();
        JLabel greenLabel = new JLabel("Choose green quantization:");
        greenLabelPanel.add(greenLabel);

        JPanel greenPanel = new JPanel(new BorderLayout());
        greenPanel.add(greenLabelPanel, BorderLayout.NORTH);
        greenPanel.add(greenQuantizationField, BorderLayout.SOUTH);

        JPanel blueLabelPanel = new JPanel();
        JLabel blueLabel = new JLabel("Choose blue quantization:");
        blueLabelPanel.add(blueLabel);

        JPanel bluePanel = new JPanel(new BorderLayout());
        bluePanel.add(blueLabelPanel, BorderLayout.NORTH);
        bluePanel.add(blueQuantizationField, BorderLayout.SOUTH);

        JPanel rgbPanel = new JPanel();
        rgbPanel.add(redPanel, BorderLayout.NORTH);
        rgbPanel.add(greenPanel, BorderLayout.SOUTH);
        rgbPanel.add(bluePanel);

        JPanel confirmButtons = new JPanel();
        confirmButtons.add(cancelButton, BorderLayout.SOUTH);
        confirmButtons.add(okButton, BorderLayout.SOUTH);
        okButton.setVisible(true);

        parametersDialog.add(rgbPanel, BorderLayout.NORTH);
        parametersDialog.add(confirmButtons, BorderLayout.SOUTH);

        parametersDialog.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, CHOOSE_DIALOG_HEIGHT));
        parametersDialog.pack();
        parametersDialog.setLocationRelativeTo(null);
        parametersDialog.setVisible(true);
    }

    private boolean checkIfInputIsValid(String input, JDialog dialog) {
        try {
            double value = Integer.parseInt(input);
            if (value < 2 || value > 128) {
                dialog.dispose();
                JOptionPane.showMessageDialog(null, "Color component quantization should be a value in [2; 128]", "Incorrect parameter", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException exception) {
            dialog.dispose();
            JOptionPane.showMessageDialog(null, "Color component quantization should be a value in [2; 128]", "Incorrect parameter", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
