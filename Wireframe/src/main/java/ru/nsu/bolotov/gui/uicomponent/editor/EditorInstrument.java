package ru.nsu.bolotov.gui.uicomponent.editor;

import ru.nsu.bolotov.gui.bspline.EditorPanel;

import javax.swing.*;

public interface EditorInstrument {
    JButton getInstrumentButton();
    JMenuItem getMenuButton();
    String getInstrumentName();
    void injectActionListeners(JFrame frame, EditorPanel editorPanel);
}
