package ru.nsu.bolotov.model.instrument.impl;

import ru.nsu.bolotov.model.instrument.Instrument;

import javax.swing.*;

public class GaussSmoothing implements Instrument {
    private final JButton instrumentButton;

    public GaussSmoothing() {
        this.instrumentButton = new JButton();
        this.instrumentButton.setToolTipText(this.getInstrumentName());
    }

    @Override
    public JButton getButton() {
        return instrumentButton;
    }

    @Override
    public String getInstrumentName() {
        return this.getClass().getSimpleName();
    }
}
