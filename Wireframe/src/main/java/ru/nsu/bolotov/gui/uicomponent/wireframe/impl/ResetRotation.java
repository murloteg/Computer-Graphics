package ru.nsu.bolotov.gui.uicomponent.wireframe.impl;

import ru.nsu.bolotov.gui.uicomponent.wireframe.WireframePanelInstrument;
import ru.nsu.bolotov.gui.wireframe.WireframeViewPanel;

import javax.swing.*;

public class ResetRotation implements WireframePanelInstrument {
    private final JButton instrumentButton;
    private final JMenuItem menuButton;

    public ResetRotation() {
        String instrumentName = this.getInstrumentName();
        this.instrumentButton = new JButton();
        this.instrumentButton.setToolTipText(instrumentName);
        this.menuButton = new JMenuItem(instrumentName);
    }

    @Override
    public JButton getInstrumentButton() {
        return this.instrumentButton;
    }

    @Override
    public JMenuItem getMenuButton() {
        return this.menuButton;
    }

    @Override
    public String getInstrumentName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void injectActionListeners(JFrame frame, WireframeViewPanel wireframeViewPanel) {
        instrumentButton.addActionListener(event -> {
            wireframeViewPanel.resetRotationMatrix();
        });

        menuButton.addActionListener(event -> {
            wireframeViewPanel.resetRotationMatrix();
        });
    }
}
