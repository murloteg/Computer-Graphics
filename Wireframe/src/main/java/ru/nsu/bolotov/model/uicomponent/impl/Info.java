package ru.nsu.bolotov.model.uicomponent.impl;

import ru.nsu.bolotov.gui.bspline.EditorPanel;
import ru.nsu.bolotov.model.uicomponent.EditorInstrument;

import javax.swing.*;
import java.awt.*;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_DIALOG_SIZE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.ABOUT_BUTTON;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.ABOUT_PROGRAM_TEXT;

public class Info implements EditorInstrument {
    private final JButton instrumentButton;
    private final JMenuItem menuButton;

    public Info() {
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
            showInfoDialog(frame);
        });

        menuButton.addActionListener(event -> {
            showInfoDialog(frame);
        });
    }

    private void showInfoDialog(JFrame frame) {
        JDialog aboutDialogWindow = new JDialog(frame, ABOUT_BUTTON);
        JTextArea aboutTextArea = new JTextArea(ABOUT_PROGRAM_TEXT);
        aboutTextArea.setEnabled(false);
        aboutTextArea.setDisabledTextColor(Color.BLACK);

        aboutDialogWindow.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, STANDARD_DIALOG_SIZE));
        aboutDialogWindow.add(aboutTextArea);

        aboutDialogWindow.pack();
        aboutDialogWindow.setLocationRelativeTo(null);
        aboutDialogWindow.setVisible(true);
    }
}
