package ru.nsu.bolotov.view;

import ru.nsu.bolotov.util.UtilConsts;
import ru.nsu.bolotov.view.panel.DrawablePanel;
import ru.nsu.bolotov.view.toolbar.ToolBar;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
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

        addInstrumentsToolBar(mainFrame, mainPanel);
        addMenuBar(mainFrame);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void addInstrumentsToolBar(JFrame frame, DrawablePanel drawablePanel) {
        ToolBar toolBar = new ToolBar(frame, drawablePanel);
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
