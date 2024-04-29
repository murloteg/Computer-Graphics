package ru.nsu.bolotov.model.uicomponent.impl;

import ru.nsu.bolotov.gui.bspline.EditorPanel;
import ru.nsu.bolotov.gui.bspline.MovingMode;
import ru.nsu.bolotov.model.uicomponent.EditorInstrument;

import javax.swing.*;

public class CreatePoints implements EditorInstrument {
    private final JButton instrumentButton;
    private final JMenuItem menuButton;

    public CreatePoints() {
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
    public void injectActionListeners(JFrame frame, EditorPanel editorPanel) {
        instrumentButton.addActionListener(event -> {
            editorPanel.changeMovingMode(MovingMode.STOP_MOVING);
        });

        menuButton.addActionListener(event -> {
            editorPanel.changeMovingMode(MovingMode.STOP_MOVING);
        });
    }
}
