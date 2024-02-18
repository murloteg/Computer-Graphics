package ru.nsu.bolotov.view;

import ru.nsu.bolotov.model.paintmode.PaintMode;
import ru.nsu.bolotov.util.UtilConsts;
import ru.nsu.bolotov.view.dialog.ParametersDialog;
import ru.nsu.bolotov.view.panel.DrawablePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_DIALOG_SIZE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.APPLICATION_TITLE;

public class GUI {
    private final JFrame mainFrame;

    public GUI() {
        mainFrame = new JFrame(APPLICATION_TITLE);
        mainFrame.setMinimumSize(new Dimension(UtilConsts.DimensionConsts.MIN_WINDOW_WIDTH, UtilConsts.DimensionConsts.MIN_WINDOW_HEIGHT));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainFrame.setLayout(new BorderLayout());
        DrawablePanel mainPanel = new DrawablePanel();

//        JScrollPane scrollPane = new JScrollPane(mainPanel);
//        scrollPane.setPreferredSize(new Dimension(100, 100));
//        mainFrame.add(scrollPane, BorderLayout.PAGE_END);

        mainFrame.add(mainPanel, BorderLayout.CENTER);
        addInstrumentsTool(mainFrame, mainPanel);
        addMenuBar(mainFrame);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void addInstrumentsTool(JFrame frame, DrawablePanel panel) {
        JToolBar instrumentsToolBar = new JToolBar("Instruments");
        ClassLoader guiClassLoader = this.getClass().getClassLoader();

        URL fillIconUrl = guiClassLoader.getResource("icons/fill-icon.png");
        ImageIcon fillIcon = new ImageIcon(Objects.requireNonNull(fillIconUrl));

        URL eraserIconUrl = guiClassLoader.getResource("icons/eraser-icon.png");
        ImageIcon eraserIcon = new ImageIcon(Objects.requireNonNull(eraserIconUrl));

        URL stampIconUrl = guiClassLoader.getResource("icons/stamp-icon.png");
        ImageIcon stampIcon = new ImageIcon(Objects.requireNonNull(stampIconUrl));

        URL lineIconUrl = guiClassLoader.getResource("icons/line-icon.png");
        ImageIcon lineIcon = new ImageIcon(Objects.requireNonNull(lineIconUrl));

        URL brushIconUrl = guiClassLoader.getResource("icons/brush-icon.png");
        ImageIcon brushIcon = new ImageIcon(Objects.requireNonNull(brushIconUrl));

        URL settingsIconUrl = guiClassLoader.getResource("icons/settings-icon.png");
        ImageIcon settingsIcon = new ImageIcon(Objects.requireNonNull(settingsIconUrl));

        JButton brushButton = new JButton(brushIcon);
        JButton lineButton = new JButton(lineIcon);
        JButton stampButton = new JButton(stampIcon);
        JButton fillButton = new JButton(fillIcon);
        JButton eraserButton = new JButton(eraserIcon);
        JButton settingsButton = new JButton(settingsIcon);

        eraserButton.addActionListener(event -> {
            panel.resetPanelState();
        });

        brushButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.BRUSH);
        });

        lineButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.LINE);
        });

        fillButton.addActionListener(event -> {
            panel.setPaintMode(PaintMode.FILL);
        });

        settingsButton.addActionListener(event -> {
            ParametersDialog parametersDialog = new ParametersDialog(frame, "Settings", panel);
            parametersDialog.setVisible(true);
        });

        brushButton.setToolTipText("Brush");
        lineButton.setToolTipText("Line");
        stampButton.setToolTipText("Stamp");
        fillButton.setToolTipText("Fill");
        eraserButton.setToolTipText("Clean");
        settingsButton.setToolTipText("Settings");

        JPanel toolBarPanel = new JPanel();
        brushButton.setPreferredSize(new Dimension(40, 40));
        lineButton.setPreferredSize(new Dimension(40, 40));
        stampButton.setPreferredSize(new Dimension(40, 40));
        fillButton.setPreferredSize(new Dimension(40, 40));
        eraserButton.setPreferredSize(new Dimension(40, 40));
        settingsButton.setPreferredSize(new Dimension(40, 40));

        toolBarPanel.add(brushButton);
        toolBarPanel.add(lineButton);
        toolBarPanel.add(stampButton);
        toolBarPanel.add(fillButton);
        toolBarPanel.add(eraserButton);
        toolBarPanel.add(settingsButton);

//        JColorChooser colorChooser = new JColorChooser(Color.BLACK);
//        toolBarPanel.add(colorChooser);

        JSeparator separator = new JToolBar.Separator(new Dimension(50, 32));
        toolBarPanel.add(separator);

        // TODO: divide and make refactoring for this part
        String colorsDirectoryName = "icons/colors/";

        java.util.List<String> fileNames = new ArrayList<>();
        Path colorsDirectoryPath;
        try {
            colorsDirectoryPath = Paths.get(ClassLoader.getSystemResource(colorsDirectoryName).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (DirectoryStream<Path> colorsDirectory = Files.newDirectoryStream(Paths.get(colorsDirectoryPath.toUri()))) {
            for (Path colorPath : colorsDirectory) {
                fileNames.add(colorPath.getFileName().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }

        for (String fileName : fileNames) {
            URL imageURL = guiClassLoader.getResource(colorsDirectoryName + fileName);
            JButton colorButton = new JButton(new ImageIcon(Objects.requireNonNull(imageURL)));
            String[] partsOfFileName = fileName.split("-");
            colorButton.setToolTipText(partsOfFileName[0]);
            int rgbColor;
            try {
                BufferedImage image = ImageIO.read(imageURL);
                rgbColor = image.getRGB(image.getWidth() / 2, image.getHeight() / 2);
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO
            }

            colorButton.addActionListener(event -> {
                panel.setGeneralColor(new Color(rgbColor));
                frame.update(frame.getGraphics());
            });
            toolBarPanel.add(colorButton);
        }

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
        menuBar.add(helpBar);
        frame.setJMenuBar(menuBar);
    }
}
