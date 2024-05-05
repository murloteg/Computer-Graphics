package ru.nsu.bolotov.gui.uicomponent.wireframe.impl;

import ru.nsu.bolotov.gui.uicomponent.wireframe.WireframePanelInstrument;
import ru.nsu.bolotov.gui.wireframe.WireframeViewPanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Open implements WireframePanelInstrument {
    private final JButton instrumentButton;
    private final JMenuItem menuButton;

    public Open() {
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
            openFile(frame, wireframeViewPanel);
        });

        menuButton.addActionListener(event -> {
            openFile(frame, wireframeViewPanel);
        });
    }

    private void openFile(JFrame frame, WireframeViewPanel wireframeViewPanel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON";
            }
        });

        fileChooser.showOpenDialog(frame);
        File selectedFile = fileChooser.getSelectedFile();
        wireframeViewPanel.loadProgramStateFromJson(selectedFile);
    }
}
