package ru.nsu.bolotov.model.uicomponent.instrument.impl;

import org.apache.commons.lang3.StringUtils;
import ru.nsu.bolotov.model.uicomponent.instrument.DialogEnabled;
import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.model.filter.mode.FilterMode;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.CHOOSE_DIALOG_HEIGHT;

public class FloydSteinbergDithering implements Instrument, DialogEnabled {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;
    private final List<?> defaultInstrumentParameters = List.of(2, 2, 2);

    public FloydSteinbergDithering(ImagePanel imagePanel) {
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
            imagePanel.setFilterMode(FilterMode.FLOYD_STEINBERG_DITHERING);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            menuButton.setSelected(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });

        menuButton.addActionListener(event -> {
            showDialog();
            imagePanel.setFilterMode(FilterMode.FLOYD_STEINBERG_DITHERING);
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

        JPanel redPanel = createColorPanel(redQuantizationField, redQuantization, "red");
        JPanel greenPanel = createColorPanel(greenQuantizationField, greenQuantization, "green");
        JPanel bluePanel = createColorPanel(blueQuantizationField, blueQuantization, "blue");

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

    private JPanel createColorPanel(JTextField colorQuantizationField, String colorQuantization, String colorName) {
        JPanel colorLabelPanel = new JPanel();
        JLabel colorLabel = new JLabel(String.format("Choose %s quantization:", colorName));
        colorLabelPanel.add(colorLabel);

        JSlider colorQuantizationSlider = new JSlider(2, 128);
        colorQuantizationSlider.setValue(Integer.parseInt(colorQuantization));
        colorQuantizationSlider.addChangeListener(event -> {
            colorQuantizationField.setText(String.valueOf(colorQuantizationSlider.getValue()));
        });

        colorQuantizationField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                String inputValue = colorQuantizationField.getText();
                if (StringUtils.isNumeric(inputValue)) {
                    int numericValue = Integer.parseInt(inputValue);
                    colorQuantizationSlider.setValue(numericValue);
                }
            }
        });

        JPanel colorSliderPanel = new JPanel();
        colorSliderPanel.add(colorQuantizationSlider, BorderLayout.NORTH);

        JPanel colorPanel = new JPanel(new BorderLayout());
        colorPanel.add(colorLabelPanel, BorderLayout.NORTH);
        colorPanel.add(colorQuantizationField, BorderLayout.CENTER);
        colorPanel.add(colorSliderPanel, BorderLayout.SOUTH);
        return colorPanel;
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
