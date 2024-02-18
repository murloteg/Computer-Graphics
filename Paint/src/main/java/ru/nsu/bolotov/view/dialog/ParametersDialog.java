package ru.nsu.bolotov.view.dialog;

import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.swing.*;
import java.awt.*;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_DIALOG_SIZE;

public class ParametersDialog extends JDialog {
    private final DrawablePanel drawablePanel;
    private int lineSize;

    public ParametersDialog(JFrame owner, String title, DrawablePanel drawablePanel) {
        super(owner, title);
        this.drawablePanel = drawablePanel;

        JComboBox<Integer> lineSizeComboBox = new JComboBox<>();
        for (int px = 1; px < 30; ++px) {
            lineSizeComboBox.addItem(px);
        }

        JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(60, 40));

        okButton.addActionListener(event -> {
            drawablePanel.setLineSize((Integer) lineSizeComboBox.getSelectedItem());
            this.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 40));

        cancelButton.addActionListener(event -> {
            this.dispose();
        });

        JLabel lineSizeLabel = new JLabel("Select line size (px):");

        JPanel lineSizeDialogPanel = new JPanel();
        lineSizeDialogPanel.add(lineSizeLabel, BorderLayout.NORTH);
        lineSizeDialogPanel.add(lineSizeComboBox, BorderLayout.CENTER);
        this.add(lineSizeDialogPanel, BorderLayout.NORTH);

        JPanel buttonsDialogPanel = new JPanel();
        buttonsDialogPanel.add(okButton, BorderLayout.SOUTH);
        buttonsDialogPanel.add(cancelButton, BorderLayout.SOUTH);
        this.add(buttonsDialogPanel, BorderLayout.SOUTH);

        this.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, STANDARD_DIALOG_SIZE));
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
