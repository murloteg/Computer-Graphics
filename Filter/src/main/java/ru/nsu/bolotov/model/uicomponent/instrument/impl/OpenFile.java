package ru.nsu.bolotov.model.uicomponent.instrument.impl;

import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;

public class OpenFile implements Instrument {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;

    public OpenFile(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        String instrumentName = this.getInstrumentName();

        this.instrumentButton = new JButton();
        this.instrumentButton.setToolTipText(instrumentName);
        this.menuButton = new JRadioButtonMenuItem(instrumentName);
    }

    @Override
    public JButton getInstrumentButton() {
        return instrumentButton;
    }

    @Override
    public JRadioButtonMenuItem getMenuButton() {
        return menuButton;
    }

    @Override
    public String getInstrumentName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void injectActionListeners(List<Instrument> instrumentList, ButtonGroup buttonGroup) {
        buttonGroup.add(menuButton);
        instrumentButton.addActionListener(event -> {
            openFile();
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            menuButton.setSelected(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });

        menuButton.addActionListener(event -> {
            openFile();
            menuButton.setSelected(true);
            instrumentButton.setSelected(true);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });
    }

    @Override
    public void changeSelectState() {
        instrumentButton.setSelected(false);
    }

    private void openFile() {
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

        fileChooser.showOpenDialog(imagePanel);
        File selectedFile = fileChooser.getSelectedFile();
        imagePanel.loadFileContent(selectedFile, true);
    }
}
