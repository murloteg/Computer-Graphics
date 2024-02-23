package ru.nsu.bolotov.view.toolbar;

import ru.nsu.bolotov.model.paintmode.PaintMode;
import ru.nsu.bolotov.model.polygon.PolygonForm;
import ru.nsu.bolotov.view.dialog.ParametersDialog;
import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolBar {
    private final JToolBar instrumentsToolBar;

    public ToolBar(JFrame frame, DrawablePanel drawablePanel) {
        this.instrumentsToolBar = new JToolBar("Instruments");
        ClassLoader guiClassLoader = this.getClass().getClassLoader();

        URL fillIconUrl = guiClassLoader.getResource("icons/fill-icon.png");
        ImageIcon fillIcon = new ImageIcon(Objects.requireNonNull(fillIconUrl));

        URL eraserIconUrl = guiClassLoader.getResource("icons/eraser-icon.png");
        ImageIcon eraserIcon = new ImageIcon(Objects.requireNonNull(eraserIconUrl));

        URL polygonStampIconUrl = guiClassLoader.getResource("icons/stamp-icon.png");
        ImageIcon polygonStampIcon = new ImageIcon(Objects.requireNonNull(polygonStampIconUrl));

        URL starStampIconUrl = guiClassLoader.getResource("icons/star-icon.png");
        ImageIcon starStampIcon = new ImageIcon(Objects.requireNonNull(starStampIconUrl));

        URL lineIconUrl = guiClassLoader.getResource("icons/line-icon.png");
        ImageIcon lineIcon = new ImageIcon(Objects.requireNonNull(lineIconUrl));

        URL brushIconUrl = guiClassLoader.getResource("icons/brush-icon.png");
        ImageIcon brushIcon = new ImageIcon(Objects.requireNonNull(brushIconUrl));

        URL settingsIconUrl = guiClassLoader.getResource("icons/settings-icon.png");
        ImageIcon settingsIcon = new ImageIcon(Objects.requireNonNull(settingsIconUrl));

        JButton brushButton = new JButton(brushIcon);
        JButton lineButton = new JButton(lineIcon);
        JButton polygonStampButton = new JButton(polygonStampIcon);
        JButton starStampButton = new JButton(starStampIcon);
        JButton fillButton = new JButton(fillIcon);
        JButton eraserButton = new JButton(eraserIcon);
        JButton settingsButton = new JButton(settingsIcon);

        List<JButton> instruments = new ArrayList<>();
        instruments.add(brushButton);
        instruments.add(lineButton);
        instruments.add(polygonStampButton);
        instruments.add(starStampButton);
        instruments.add(fillButton);
        instruments.add(eraserButton);
        instruments.add(settingsButton);

        eraserButton.addActionListener(event -> {
            drawablePanel.resetPanelState();
        });

        brushButton.addActionListener(event -> {
            drawablePanel.setPaintMode(PaintMode.BRUSH);
            brushButton.getModel().setSelected(true);
            brushButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, brushButton);
        });

        lineButton.addActionListener(event -> {
            drawablePanel.setPaintMode(PaintMode.LINE);
            lineButton.getModel().setSelected(true);
            lineButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, lineButton);
        });

        polygonStampButton.addActionListener(event -> {
            drawablePanel.setPaintMode(PaintMode.POLYGON);
            drawablePanel.setPolygonForm(PolygonForm.CONVEX);
            polygonStampButton.getModel().setSelected(true);
            polygonStampButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, polygonStampButton);
        });

        starStampButton.addActionListener(event -> {
            drawablePanel.setPaintMode(PaintMode.POLYGON);
            drawablePanel.setPolygonForm(PolygonForm.STAR);
            starStampButton.getModel().setSelected(true);
            starStampButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, starStampButton);
        });

        fillButton.addActionListener(event -> {
            drawablePanel.setPaintMode(PaintMode.FILL);
            fillButton.getModel().setSelected(true);
            fillButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, fillButton);
        });

        settingsButton.addActionListener(event -> {
            ParametersDialog parametersDialog = new ParametersDialog(frame, "Settings", drawablePanel);
            parametersDialog.setVisible(true);
        });

        brushButton.setToolTipText("Brush");
        lineButton.setToolTipText("Line");
        polygonStampButton.setToolTipText("Polygon Stamp");
        starStampButton.setToolTipText("Star Stamp");
        fillButton.setToolTipText("Fill");
        eraserButton.setToolTipText("Clean");
        settingsButton.setToolTipText("Settings");

        JPanel toolBarPanel = new JPanel();
        brushButton.setPreferredSize(new Dimension(40, 40));
        lineButton.setPreferredSize(new Dimension(40, 40));
        polygonStampButton.setPreferredSize(new Dimension(40, 40));
        starStampButton.setPreferredSize(new Dimension(40, 40));
        fillButton.setPreferredSize(new Dimension(40, 40));
        eraserButton.setPreferredSize(new Dimension(40, 40));
        settingsButton.setPreferredSize(new Dimension(40, 40));

        toolBarPanel.add(brushButton);
        toolBarPanel.add(lineButton);
        toolBarPanel.add(polygonStampButton);
        toolBarPanel.add(starStampButton);
        toolBarPanel.add(fillButton);
        toolBarPanel.add(eraserButton);
        toolBarPanel.add(settingsButton);

        JSeparator separator = new JToolBar.Separator(new Dimension(50, 32));
        toolBarPanel.add(separator);

        addColorsToToolBarPanel(toolBarPanel, frame, drawablePanel);
        instrumentsToolBar.add(toolBarPanel);
    }

    public JToolBar getInstrumentsToolBar() {
        return this.instrumentsToolBar;
    }

    private void unselectOtherInstrumentsButtons(List<JButton> instruments, JButton callerButton) {
        for (JButton instrumentButton : instruments) {
            if (!instrumentButton.equals(callerButton)) {
                instrumentButton.getModel().setSelected(false);
            }
        }
    }

    private void addColorsToToolBarPanel(JPanel toolBarPanel, JFrame frame, DrawablePanel drawablePanel) {
        List<JButton> colors = prepareColorButtons();
        for (JButton colorButton: colors) {
            colorButton.setBorderPainted(false);
            colorButton.setOpaque(true);
            colorButton.setPreferredSize(new Dimension(40, 40));
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
        chooseColorButton.setPreferredSize(new Dimension(40, 40));

        chooseColorButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(frame, "Choose color", Color.BLACK);
            if (Objects.nonNull(selectedColor)) {
                drawablePanel.setGeneralColor(selectedColor);
            }
        });
        return chooseColorButton;
    }
}
