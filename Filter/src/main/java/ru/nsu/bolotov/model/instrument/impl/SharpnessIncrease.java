package ru.nsu.bolotov.model.instrument.impl;

import ru.nsu.bolotov.model.instrument.Instrument;
import ru.nsu.bolotov.model.mode.FilterMode;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;

import javax.swing.*;
import java.util.List;

public class SharpnessIncrease implements Instrument {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;

    public SharpnessIncrease(ImagePanel imagePanel) {
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
        instrumentButton.addActionListener(event -> {
            imagePanel.setFilterMode(FilterMode.SHARPNESS_INCREASING);
            menuButton.setSelected(true);
        });

        menuButton.addActionListener(event -> {
            imagePanel.setFilterMode(FilterMode.SHARPNESS_INCREASING);
            menuButton.setSelected(true);
            instrumentButton.setSelected(true);
        });
    }
}
