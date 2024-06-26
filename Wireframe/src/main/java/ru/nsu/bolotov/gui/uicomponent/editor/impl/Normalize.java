package ru.nsu.bolotov.gui.uicomponent.editor.impl;

import ru.nsu.bolotov.gui.bspline.EditorPanel;
import ru.nsu.bolotov.gui.uicomponent.editor.EditorInstrument;

import javax.swing.*;

public class Normalize implements EditorInstrument {
    private final JButton instrumentButton;
    private final JMenuItem menuButton;

    public Normalize() {
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
            editorPanel.normalizeImage();
        });

        menuButton.addActionListener(event -> {
            editorPanel.normalizeImage();
        });
    }
}
