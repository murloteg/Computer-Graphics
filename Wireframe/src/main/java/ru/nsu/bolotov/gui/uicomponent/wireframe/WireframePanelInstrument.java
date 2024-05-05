package ru.nsu.bolotov.gui.uicomponent.wireframe;

import ru.nsu.bolotov.gui.wireframe.WireframeViewPanel;

import javax.swing.*;

public interface WireframePanelInstrument {
    JButton getInstrumentButton();
    JMenuItem getMenuButton();
    String getInstrumentName();
    void injectActionListeners(JFrame frame, WireframeViewPanel wireframeViewPanel);
}
