package ru.nsu.bolotov.model.uicomponent.instrument.impl;

import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;

import static ru.nsu.bolotov.util.UtilConsts.StringConsts.ERROR_TITLE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.INCORRECT_FILE_EXTENSION_MESSAGE;

public class SaveFile implements Instrument {
    private final ImagePanel imagePanel;
    private final JButton instrumentButton;
    private final JRadioButtonMenuItem menuButton;

    public SaveFile(ImagePanel imagePanel) {
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
            saveFile();
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            menuButton.setSelected(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });

        menuButton.addActionListener(event -> {
            saveFile();
            menuButton.setSelected(true);
            instrumentButton.getModel().setSelected(true);
            instrumentButton.setOpaque(true);
            imagePanel.setCurrentInstrument(this);
            unselectOtherInstrumentsButtons(instrumentList, this);
        });
    }

    @Override
    public void changeSelectState() {
        instrumentButton.setSelected(false);
    }

    private void saveFile() {
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

        fileChooser.showSaveDialog(imagePanel);
        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().endsWith(".png")) {
            JOptionPane.showMessageDialog(imagePanel, INCORRECT_FILE_EXTENSION_MESSAGE, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        imagePanel.saveCanvasContent(selectedFile);
    }
}
