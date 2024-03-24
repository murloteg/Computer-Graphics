package ru.nsu.bolotov.model.uicomponent.instrument.impl;

import ru.nsu.bolotov.model.uicomponent.instrument.DialogEnabled;
import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;
import ru.nsu.bolotov.view.mode.ViewMode;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;

public class ChangeViewMode implements Instrument, DialogEnabled {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;

    public ChangeViewMode(ImagePanel imagePanel) {
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
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            menuButton.setSelected(true);
            unselectOtherInstrumentsButtons(instrumentList, this);
            imagePanel.updateImageOnPanel();
        });

        menuButton.addActionListener(event -> {
            showDialog();
            menuButton.setSelected(true);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instrumentList, this);
            imagePanel.updateImageOnPanel();
        });
    }

    @Override
    public void changeSelectState() {
        instrumentButton.setSelected(false);
    }

    @Override
    public List<?> getDefaultParameters() {
        return Collections.emptyList();
    }

    @Override
    public void showDialog() {
        JDialog parametersDialog = new JDialog();
        
        JPanel labelPanel = new JPanel();
        JLabel parameterLabel = new JLabel("Choose view mode:");
        labelPanel.add(parameterLabel);

        JButton realSizeButton = new JButton("Real size");
        realSizeButton.setPreferredSize(new Dimension(BUTTON_WITH_TEXT_WIDTH, STANDARD_BUTTON_SIZE));

        realSizeButton.addActionListener(event -> {
            imagePanel.setViewMode(ViewMode.REAL_SIZE);
            parametersDialog.dispose();
        });

        JButton firToScreenButton = new JButton("Fit to screen");
        firToScreenButton.setPreferredSize(new Dimension(BUTTON_WITH_TEXT_WIDTH, STANDARD_BUTTON_SIZE));

        firToScreenButton.addActionListener(event -> {
            imagePanel.setViewMode(ViewMode.FIT_TO_SCREEN);
            parametersDialog.dispose();
        });
        
        JPanel viewModeButtons = new JPanel();
        viewModeButtons.add(firToScreenButton, BorderLayout.SOUTH);
        viewModeButtons.add(realSizeButton, BorderLayout.SOUTH);
        realSizeButton.setVisible(true);

        parametersDialog.add(labelPanel, BorderLayout.NORTH);
        parametersDialog.add(viewModeButtons, BorderLayout.SOUTH);

        parametersDialog.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, CHOOSE_DIALOG_HEIGHT));
        parametersDialog.pack();
        parametersDialog.setLocationRelativeTo(null);
        parametersDialog.setVisible(true);
    }
}
