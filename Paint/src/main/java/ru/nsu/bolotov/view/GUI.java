package ru.nsu.bolotov.view;

import ru.nsu.bolotov.model.paintmode.PaintMode;
import ru.nsu.bolotov.model.polygon.PolygonForm;
import ru.nsu.bolotov.util.UtilConsts;
import ru.nsu.bolotov.view.dialog.ParametersDialog;
import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_DIALOG_SIZE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.APPLICATION_TITLE;

public class GUI {
    private final JFrame mainFrame;

    public GUI() {
        mainFrame = new JFrame(APPLICATION_TITLE);
        mainFrame.setMinimumSize(new Dimension(UtilConsts.DimensionConsts.MIN_WINDOW_WIDTH, UtilConsts.DimensionConsts.MIN_WINDOW_HEIGHT));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        URL applicationIconUrl = this.getClass().getClassLoader().getResource("paint-logo.png");
        ImageIcon applicationIcon = new ImageIcon(Objects.requireNonNull(applicationIconUrl));

        mainFrame.setIconImage(applicationIcon.getImage());

        mainFrame.setLayout(new BorderLayout());
        DrawablePanel mainPanel = new DrawablePanel();

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.createHorizontalScrollBar();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPane.setViewportView(mainPanel);
        scrollPane.setPreferredSize(new Dimension(500,500));

        mainFrame.add(scrollPane, BorderLayout.CENTER);

//        mainFrame.add(mainPanel, BorderLayout.CENTER);
        addInstrumentsTool(mainFrame, mainPanel);
        addMenuBar(mainFrame);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void unselectOtherInstrumentsButtons(List<JButton> instruments, JButton callerButton) {
        for (JButton instrumentButton : instruments) {
            if (!instrumentButton.equals(callerButton)) {
                instrumentButton.getModel().setSelected(false);
            }
        }
    }

    private void addInstrumentsTool(JFrame frame, DrawablePanel panel) {
        JToolBar instrumentsToolBar = new JToolBar("Instruments");
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
            panel.resetPanelState();
        });

        brushButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.BRUSH);
            brushButton.getModel().setSelected(true);
            brushButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, brushButton);
        });
        // TODO: btn.getModel().setSelected(true);

        lineButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.LINE);
            lineButton.getModel().setSelected(true);
            lineButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, lineButton);
        });

        polygonStampButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.POLYGON);
            panel.setPolygonForm(PolygonForm.CONVEX);
            polygonStampButton.getModel().setSelected(true);
            polygonStampButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, polygonStampButton);
        });

        starStampButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.POLYGON);
            panel.setPolygonForm(PolygonForm.STAR);
            starStampButton.getModel().setSelected(true);
            starStampButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, starStampButton);
        });

        fillButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.FILL);
            fillButton.getModel().setSelected(true);
            fillButton.setOpaque(true);
            unselectOtherInstrumentsButtons(instruments, fillButton);
        });

        settingsButton.addActionListener(event -> {
            ParametersDialog parametersDialog = new ParametersDialog(frame, "Settings", panel);
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


        // FIXME: Button.setBackground(); BorderFactory
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

        for (JButton colorButton: colors) {
            colorButton.setBorderPainted(false);
            colorButton.setOpaque(true);
            colorButton.setPreferredSize(new Dimension(40, 40));
            colorButton.addActionListener(event -> {
                panel.setGeneralColor(colorButton.getBackground());
                frame.update(frame.getGraphics());
            });
            toolBarPanel.add(colorButton);
        }

        URL paletteIconUrl = guiClassLoader.getResource("icons/palette-icon.png");
        ImageIcon paletteIcon = new ImageIcon(Objects.requireNonNull(paletteIconUrl));

        JButton chooseColorButton = new JButton(paletteIcon);
        chooseColorButton.setToolTipText("Choose color");
        brushButton.setPreferredSize(new Dimension(40, 40));

        chooseColorButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(frame, "Choose color", Color.BLACK);
            if (Objects.nonNull(selectedColor)) {
                panel.setGeneralColor(selectedColor);
            }
        });

        toolBarPanel.add(chooseColorButton);


        ///
        instrumentsToolBar.add(toolBarPanel);
        frame.add(instrumentsToolBar, BorderLayout.PAGE_START);
    }

    private void addMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpBar = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(event -> {
            JDialog aboutDialogWindow = new JDialog(frame, UtilConsts.ButtonNameConsts.ABOUT_BUTTON);
            JLabel aboutLabel = new JLabel(UtilConsts.StringConsts.ABOUT_PROGRAM_TEXT);

            aboutDialogWindow.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, STANDARD_DIALOG_SIZE));
            aboutDialogWindow.add(aboutLabel);

            aboutDialogWindow.pack();
            aboutDialogWindow.setLocationRelativeTo(null);
            aboutDialogWindow.setVisible(true);
        });
        helpBar.add(aboutItem);

        JMenu instrumentsBar = new JMenu("Instruments");
        JMenuItem brushItem = new JMenuItem();
        JRadioButton brushButton = new JRadioButton("Brush");
        brushButton.setMinimumSize(new Dimension(40, 40));

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(brushButton);

        brushItem.add(brushButton);
        instrumentsBar.add(brushItem);
        // TODO

        menuBar.add(helpBar);
        menuBar.add(instrumentsBar);
        frame.setJMenuBar(menuBar);
    }
}
