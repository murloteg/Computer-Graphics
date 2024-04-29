package ru.nsu.bolotov.model.uicomponent.impl;

import ru.nsu.bolotov.gui.bspline.EditorPanel;
import ru.nsu.bolotov.model.uicomponent.EditorInstrument;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

import static ru.nsu.bolotov.util.UtilConsts.StringConsts.ERROR_TITLE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.INCORRECT_FILE_EXTENSION_MESSAGE;

public class Save implements EditorInstrument {
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
    public void injectActionListeners(JFrame frame, EditorPanel editorPanel) {
        instrumentButton.addActionListener(event -> {
            saveFile(frame);
        });

        menuButton.addActionListener(event -> {
            saveFile(frame);
        });
    }

    private void saveFile(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "PNG";
            }
        });

        fileChooser.showSaveDialog(frame);
//        File selectedFile = fileChooser.getSelectedFile();
//        if (!selectedFile.getName().endsWith(".png")) {
//            JOptionPane.showMessageDialog(frame, INCORRECT_FILE_EXTENSION_MESSAGE, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        imagePanel.saveCanvasContent(selectedFile);
    }
}
