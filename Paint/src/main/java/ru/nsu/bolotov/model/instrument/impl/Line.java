package ru.nsu.bolotov.model.instrument.impl;

import ru.nsu.bolotov.model.instrument.PaintInstrument;
import ru.nsu.bolotov.model.paintmode.PaintMode;
import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.swing.*;
import java.util.List;

public class Line implements PaintInstrument {
    private final DrawablePanel drawablePanel;
    private final JButton toolBarButton;
    private final JRadioButtonMenuItem menuBarButton;

    public Line(DrawablePanel drawablePanel) {
        this.drawablePanel = drawablePanel;
        String instrumentName = this.getClass().getSimpleName();

        menuBarButton = new JRadioButtonMenuItem(instrumentName);
        toolBarButton = new JButton();
        toolBarButton.setToolTipText(instrumentName);
    }

    @Override
    public JButton getToolBarButton() {
        return toolBarButton;
    }

    @Override
    public JRadioButtonMenuItem getMenuBarButton() {
        return menuBarButton;
    }

    @Override
    public void injectActionListeners(List<PaintInstrument> instruments, ButtonGroup buttonGroup) {
        buttonGroup.add(menuBarButton);
        toolBarButton.addActionListener(event -> {
            drawablePanel.setPaintMode(PaintMode.LINE);
            toolBarButton.getModel().setSelected(true);
            toolBarButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, this);
            menuBarButton.setSelected(true);
        });

        menuBarButton.addActionListener(event -> {
            menuBarButton.setSelected(true);
            drawablePanel.setPaintMode(PaintMode.LINE);
            toolBarButton.getModel().setSelected(true);
            toolBarButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, this);
        });
    }

    @Override
    public void setButtonIcon(Icon buttonIcon) {
        toolBarButton.setIcon(buttonIcon);
    }

    @Override
    public String getInstrumentName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void changeSelectState() {
        toolBarButton.getModel().setSelected(false);
    }
}
