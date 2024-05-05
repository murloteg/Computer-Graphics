package ru.nsu.bolotov.gui.uicomponent.wireframe.impl;

import ru.nsu.bolotov.gui.uicomponent.wireframe.WireframePanelInstrument;
import ru.nsu.bolotov.gui.wireframe.WireframeViewPanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import static ru.nsu.bolotov.util.UtilConsts.StringConsts.ERROR_TITLE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.INCORRECT_FILE_EXTENSION_MESSAGE;

public class Save implements WireframePanelInstrument {
    private final JButton instrumentButton;
    private final JMenuItem menuButton;

    public Save() {
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
            saveFile(frame, wireframeViewPanel);
        });

        menuButton.addActionListener(event -> {
            saveFile(frame, wireframeViewPanel);
        });
    }

    private void saveFile(JFrame frame, WireframeViewPanel wireframeViewPanel) {
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

        fileChooser.showSaveDialog(frame);
        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().endsWith(".json")) {
            JOptionPane.showMessageDialog(frame, INCORRECT_FILE_EXTENSION_MESSAGE, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        String serializerResult = wireframeViewPanel.saveProgramStateAsJsonString();
        try {
            Files.writeString(selectedFile.toPath(), serializerResult);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
