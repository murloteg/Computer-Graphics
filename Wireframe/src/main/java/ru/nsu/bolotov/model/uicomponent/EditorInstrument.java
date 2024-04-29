package ru.nsu.bolotov.model.uicomponent;

import ru.nsu.bolotov.gui.bspline.EditorPanel;

import javax.swing.*;

public interface EditorInstrument {
    JButton getInstrumentButton();
    JMenuItem getMenuButton();
    String getInstrumentName();
    void injectActionListeners(JFrame frame, EditorPanel editorPanel);
}
