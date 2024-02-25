package ru.nsu.bolotov.view;

import ru.nsu.bolotov.model.instrument.PaintInstrument;
import ru.nsu.bolotov.model.instrument.impl.*;
import ru.nsu.bolotov.util.UtilConsts;
import ru.nsu.bolotov.view.panel.DrawablePanel;
import ru.nsu.bolotov.view.toolbar.ToolBar;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.APPLICATION_TITLE;

public class GUI {
    private final JFrame mainFrame;
    private final List<PaintInstrument> instruments;

    public GUI() {
        mainFrame = new JFrame(APPLICATION_TITLE);
        Dimension minimalDimension = new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        mainFrame.setMinimumSize(minimalDimension);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        URL applicationIconUrl = this.getClass().getClassLoader().getResource("paint-logo.png");
        ImageIcon applicationIcon = new ImageIcon(Objects.requireNonNull(applicationIconUrl));
        mainFrame.setIconImage(applicationIcon.getImage());

        mainFrame.setLayout(new BorderLayout());
        DrawablePanel mainPanel = new DrawablePanel(mainFrame);

        ButtonGroup buttonGroup = new ButtonGroup();
        instruments = new ArrayList<>();
        initializeInstruments(mainPanel, buttonGroup);

        mainPanel.setPreferredSize(new Dimension(CANVAS_SIZE_WIDTH, CANVAS_SIZE_HEIGHT));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.createHorizontalScrollBar();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainFrame.add(scrollPane, BorderLayout.CENTER);

        addMenuBar(mainFrame);
        addInstrumentsToolBar(mainFrame, mainPanel);

        mainFrame.setPreferredSize(minimalDimension);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void initializeInstruments(DrawablePanel drawablePanel, ButtonGroup buttonGroup) {
        instruments.add(new Brush(drawablePanel));
        instruments.add(new Line(drawablePanel));
        instruments.add(new PolygonStamp(drawablePanel));
        instruments.add(new StarStamp(drawablePanel));
        instruments.add(new Fill(drawablePanel));
        instruments.add(new Eraser(drawablePanel));
        instruments.add(new Settings(drawablePanel));

        ClassLoader classLoader = this.getClass().getClassLoader();
        for (PaintInstrument instrument : instruments) {
            instrument.injectActionListeners(instruments, buttonGroup);
            instrument.getToolBarButton().setPreferredSize(new Dimension(STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE));

            URL instumentIconUrl = classLoader.getResource("icons/" + instrument.getInstrumentName().toLowerCase() + "-icon.png");
            ImageIcon instrumentIcon = new ImageIcon(Objects.requireNonNull(instumentIconUrl));
            instrument.setButtonIcon(instrumentIcon);
        }
    }

    private void addInstrumentsToolBar(JFrame frame, DrawablePanel drawablePanel) {
        ToolBar toolBar = new ToolBar(frame, drawablePanel, instruments);
        frame.add(toolBar.getInstrumentsToolBar(), BorderLayout.PAGE_START);
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
        for (PaintInstrument instrument : instruments) {
            instrumentsBar.add(instrument.getMenuBarButton());
        }

        menuBar.add(helpBar);
        menuBar.add(instrumentsBar);
        frame.setJMenuBar(menuBar);
    }
}
