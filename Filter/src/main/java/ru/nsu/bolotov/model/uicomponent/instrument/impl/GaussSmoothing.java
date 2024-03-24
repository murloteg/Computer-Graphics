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
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_BUTTON_SIZE;

public class GaussSmoothing implements Instrument, DialogEnabled {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;
    private final List<?> defaultInstrumentParameters = List.of(3);

    public GaussSmoothing(ImagePanel imagePanel) {
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
            imagePanel.setFilterMode(FilterMode.GAUSS_SMOOTHING);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            menuButton.setSelected(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });

        menuButton.addActionListener(event -> {
            showDialog();
            imagePanel.setFilterMode(FilterMode.GAUSS_SMOOTHING);
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
        JComboBox<Integer> gaussComboBox = new JComboBox<>();
        for (int i = 3; i <= 11; i += 2) {
            gaussComboBox.addItem(i);
        }

        List<?> parameters = imagePanel.getPreviousParametersForInstrument(getInstrumentName());
        int matrixSize = (int) Objects.requireNonNull(parameters.get(0));
        gaussComboBox.setSelectedItem(matrixSize);

        JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(CONFIRM_BUTTON_WIDTH, STANDARD_BUTTON_SIZE));

        okButton.addActionListener(event -> {
            imagePanel.addStateToApplicationParameters(getInstrumentName(), List.of((Integer) Objects.requireNonNull(gaussComboBox.getSelectedItem())));
            imagePanel.setCurrentInstrument(this);
            parametersDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(CONFIRM_BUTTON_WIDTH, STANDARD_BUTTON_SIZE));

        cancelButton.addActionListener(event -> {
            parametersDialog.dispose();
        });

        JPanel labelPanel = new JPanel();
        JLabel parameterLabel = new JLabel("Choose matrix size:");
        labelPanel.add(parameterLabel);

        JPanel parameterPanel = new JPanel(new BorderLayout());
        parameterPanel.add(labelPanel, BorderLayout.NORTH);
        parameterPanel.add(gaussComboBox, BorderLayout.SOUTH);

        JPanel confirmButtons = new JPanel();
        confirmButtons.add(cancelButton, BorderLayout.SOUTH);
        confirmButtons.add(okButton, BorderLayout.SOUTH);
        okButton.setVisible(true);

        parametersDialog.add(parameterPanel, BorderLayout.NORTH);
        parametersDialog.add(confirmButtons, BorderLayout.SOUTH);

        parametersDialog.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, CHOOSE_DIALOG_HEIGHT));
        parametersDialog.pack();
        parametersDialog.setLocationRelativeTo(null);
        parametersDialog.setVisible(true);
    }
}
