package ru.nsu.bolotov.view.toolbar;

import ru.nsu.bolotov.model.instrument.PaintInstrument;
import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_BUTTON_SIZE;

public class ToolBar {
    private final JToolBar instrumentsToolBar;

    public ToolBar(JFrame frame, DrawablePanel drawablePanel, List<PaintInstrument> instruments) {
        this.instrumentsToolBar = new JToolBar("Instruments");

        JPanel toolBarPanel = new JPanel();
        for (PaintInstrument instrument : instruments) {
            toolBarPanel.add(instrument.getToolBarButton());
        }
        JSeparator separator = new JToolBar.Separator(new Dimension(50, 32));
        toolBarPanel.add(separator);

        addColorsToToolBarPanel(toolBarPanel, frame, drawablePanel);
        instrumentsToolBar.add(toolBarPanel);
    }

    public JToolBar getInstrumentsToolBar() {
        return this.instrumentsToolBar;
    }

    private void addColorsToToolBarPanel(JPanel toolBarPanel, JFrame frame, DrawablePanel drawablePanel) {
        List<JButton> colors = prepareColorButtons();
        for (JButton colorButton: colors) {
            colorButton.setBorderPainted(false);
            colorButton.setOpaque(true);
            colorButton.setPreferredSize(new Dimension(STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE));
            colorButton.addActionListener(event -> {
                drawablePanel.setGeneralColor(colorButton.getBackground());
                frame.update(frame.getGraphics());
            });
            toolBarPanel.add(colorButton);
        }
        JButton chooseColorButton = prepareChooseColorButton(frame, drawablePanel);
        toolBarPanel.add(chooseColorButton);
    }

    private List<JButton> prepareColorButtons() {
        List<JButton> colors = new ArrayList<>();

        JButton blackColorButton = new JButton();
        blackColorButton.setBackground(Color.BLACK);
        blackColorButton.setToolTipText("Black");
        colors.add(blackColorButton);

        JButton blueColorButton = new JButton();
        blueColorButton.setBackground(Color.BLUE);
        blueColorButton.setToolTipText("Blue");
        colors.add(blueColorButton);

        JButton redColorButton = new JButton();
        redColorButton.setBackground(Color.RED);
        redColorButton.setToolTipText("Red");
        colors.add(redColorButton);

        JButton greenColorButton = new JButton();
        greenColorButton.setBackground(Color.GREEN);
        greenColorButton.setToolTipText("Green");
        colors.add(greenColorButton);

        JButton cyanColorButton = new JButton();
        cyanColorButton.setBackground(Color.CYAN);
        cyanColorButton.setToolTipText("Cyan");
        colors.add(cyanColorButton);

        JButton magentaColorButton = new JButton();
        magentaColorButton.setBackground(Color.MAGENTA);
        magentaColorButton.setToolTipText("Magenta");
        colors.add(magentaColorButton);

        JButton yellowColorButton = new JButton();
        yellowColorButton.setBackground(Color.YELLOW);
        yellowColorButton.setToolTipText("Yellow");
        colors.add(yellowColorButton);

        JButton whiteColorButton = new JButton();
        whiteColorButton.setBackground(Color.WHITE);
        whiteColorButton.setToolTipText("White");
        colors.add(whiteColorButton);
        return colors;
    }

    private JButton prepareChooseColorButton(JFrame frame, DrawablePanel drawablePanel) {
        ClassLoader guiClassLoader = this.getClass().getClassLoader();
        URL paletteIconUrl = guiClassLoader.getResource("icons/palette-icon.png");
        ImageIcon paletteIcon = new ImageIcon(Objects.requireNonNull(paletteIconUrl));

        JButton chooseColorButton = new JButton(paletteIcon);
        chooseColorButton.setToolTipText("Choose color");
        chooseColorButton.setPreferredSize(new Dimension(STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE));

        chooseColorButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(frame, "Choose color", Color.BLACK);
            if (Objects.nonNull(selectedColor)) {
                drawablePanel.setGeneralColor(selectedColor);
            }
        });
        return chooseColorButton;
    }
}
