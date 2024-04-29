package ru.nsu.bolotov.model.uicomponent.impl;

import ru.nsu.bolotov.gui.bspline.EditorPanel;
import ru.nsu.bolotov.model.uicomponent.EditorInstrument;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Open implements EditorInstrument {
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
    public void injectActionListeners(JFrame frame, EditorPanel editorPanel) {
        instrumentButton.addActionListener(event -> {
            openFile(frame);
        });

        menuButton.addActionListener(event -> {
            openFile(frame);
        });
    }

    private void openFile(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".png") || file.getName().endsWith(".jpg") ||
                        file.getName().endsWith(".bmp") || file.getName().endsWith(".gif");
            }

            @Override
            public String getDescription() {
                return "PNG, JPG, BMP, GIF";
            }
        });

        fileChooser.showOpenDialog(frame);
        File selectedFile = fileChooser.getSelectedFile();
//        imagePanel.loadFileContent(selectedFile);
    }
}
