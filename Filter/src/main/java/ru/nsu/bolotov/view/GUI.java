package ru.nsu.bolotov.view;

import ru.nsu.bolotov.model.instrument.Instrument;
import ru.nsu.bolotov.model.instrument.impl.*;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.ButtonNameConsts.ABOUT_BUTTON;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.*;

public class GUI {
    private final JFrame mainFrame;
    private final java.util.List<Instrument> instrumentList = new ArrayList<>();

    public GUI() {
        mainFrame = new JFrame(APPLICATION_TITLE);

        mainFrame.setMinimumSize(new Dimension(MINIMAL_WEIGHT, MINIMAL_HEIGHT));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        ImagePanel imagePanel = new ImagePanel(scrollPane, mainFrame);
        imagePanel.setMinimumSize(new Dimension(600, 600));
        imagePanel.setVisible(true);

        scrollPane.createHorizontalScrollBar();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        scrollPane.setBorder(BorderFactory.createDashedBorder(Color.BLACK, 2f, 1.5f, 1.5f, true));

        mainFrame.add(scrollPane, BorderLayout.CENTER);
        JSeparator leftSeparator = new JSeparator(SwingConstants.VERTICAL);
        JSeparator rightSeparator = new JSeparator(SwingConstants.VERTICAL);
        mainFrame.add(leftSeparator, BorderLayout.WEST);
        mainFrame.add(rightSeparator, BorderLayout.EAST);
        JSeparator downSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        mainFrame.add(downSeparator, BorderLayout.SOUTH);

        ButtonGroup buttonGroup = new ButtonGroup();
        initializeInstruments(imagePanel, buttonGroup);
        addMenuBar(mainFrame, imagePanel);
        addToolBar(mainFrame, imagePanel);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    public void startGUI() {
        mainFrame.setVisible(true);
    }

    private void initializeInstruments(ImagePanel imagePanel, ButtonGroup buttonGroup) {
        OpenFile openFile = new OpenFile(imagePanel);
        SaveFile saveFile = new SaveFile(imagePanel);
        ChangeViewMode changeViewMode = new ChangeViewMode(imagePanel);
        WhiteAndBlack whiteAndBlack = new WhiteAndBlack(imagePanel);
        Negative negative = new Negative(imagePanel);
        GaussSmoothing gaussSmoothing = new GaussSmoothing(imagePanel);
        SharpnessIncrease sharpnessIncrease = new SharpnessIncrease(imagePanel);
        Embossing embossing = new Embossing(imagePanel);
        GammaCorrection gammaCorrection = new GammaCorrection(imagePanel);
        RobertsOperator robertsOperator = new RobertsOperator(imagePanel);
        SobelOperator sobelOperator = new SobelOperator(imagePanel);
        FloydSteinbergDithering floydSteinbergDithering = new FloydSteinbergDithering(imagePanel);
        OrderlyDithering orderlyDithering = new OrderlyDithering(imagePanel);
        AquaRealization aquaRealization = new AquaRealization(imagePanel);
        Rotate rotate = new Rotate(imagePanel);
        RetroEffect retroEffect = new RetroEffect(imagePanel);
        StartProcessing startProcessing = new StartProcessing(imagePanel);

        instrumentList.add(openFile);
        instrumentList.add(saveFile);
        instrumentList.add(changeViewMode);
        instrumentList.add(whiteAndBlack);
        instrumentList.add(negative);
        instrumentList.add(gaussSmoothing);
        instrumentList.add(sharpnessIncrease);
        instrumentList.add(embossing);
        instrumentList.add(gammaCorrection);
        instrumentList.add(robertsOperator);
        instrumentList.add(sobelOperator);
        instrumentList.add(floydSteinbergDithering);
        instrumentList.add(orderlyDithering);
        instrumentList.add(aquaRealization);
        instrumentList.add(rotate);
        instrumentList.add(retroEffect);
        instrumentList.add(startProcessing);

        for (Instrument instrument : instrumentList) {
            instrument.injectActionListeners(instrumentList, buttonGroup);
        }
    }

    private void addMenuBar(JFrame frame, ImagePanel imagePanel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu helpBar = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(event -> {
            JDialog aboutDialogWindow = new JDialog(frame, ABOUT_BUTTON);
            JTextArea aboutTextArea = new JTextArea(ABOUT_PROGRAM_TEXT);
            aboutTextArea.setEnabled(false);
            aboutTextArea.setDisabledTextColor(Color.BLACK);

            aboutDialogWindow.setMinimumSize(new Dimension(STANDARD_DIALOG_SIZE, STANDARD_DIALOG_SIZE));
            aboutDialogWindow.add(aboutTextArea);

            aboutDialogWindow.pack();
            aboutDialogWindow.setLocationRelativeTo(null);
            aboutDialogWindow.setVisible(true);
        });
        helpBar.add(aboutItem);

        JMenu fileBar = new JMenu("File");
        JMenuItem saveFileItem = new JMenuItem("Save");
        saveFileItem.addActionListener(event -> {
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

            fileChooser.showSaveDialog(mainFrame);
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".png")) {
                JOptionPane.showMessageDialog(mainFrame, INCORRECT_FILE_EXTENSION_MESSAGE, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            imagePanel.saveCanvasContent(selectedFile);
        });
        fileBar.add(saveFileItem);

        JMenuItem openFileItem = new JMenuItem("Open");
        openFileItem.addActionListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(".png") || file.getName().endsWith(".jpg") ||
                            file.getName().endsWith(".bmp") || file.getName().endsWith(".gif");
                }

                @Override
                public String getDescription() {
                    return "PNG, JPG, BMP, GIF";
                }
            });

            fileChooser.showOpenDialog(mainFrame);
            File selectedFile = fileChooser.getSelectedFile();
            imagePanel.loadFileContent(selectedFile, true);
        });
        fileBar.add(openFileItem);

        JMenu instrumentsBar = new JMenu("Instruments");
        for (Instrument instrument : instrumentList) {
            instrumentsBar.add(instrument.getMenuButton());
        }

        menuBar.add(fileBar);
        menuBar.add(instrumentsBar);
        menuBar.add(helpBar);
        frame.setJMenuBar(menuBar);
    }

    private void addToolBar(JFrame frame, ImagePanel imagePanel) { // FIXME
        ClassLoader currentClassLoader = this.getClass().getClassLoader();

        JToolBar toolBar = new JToolBar("Instruments");
        JPanel toolBarPanel = new JPanel();

        for (Instrument instrument : instrumentList) {
            JButton instrumentButton = instrument.getInstrumentButton();
            URL buttonFileURL = currentClassLoader.getResource("icons/icon-" + instrument.getInstrumentName().toLowerCase() + ".png");
            ImageIcon buttonIcon = new ImageIcon(Objects.requireNonNull(buttonFileURL));
            instrumentButton.setIcon(buttonIcon);
            instrumentButton.setPreferredSize(new Dimension(45, 45));
            instrumentButton.setBorderPainted(true);
            if (instrument.equals(instrumentList.get(instrumentList.size() - 1))) {
                JSeparator separator = new JToolBar.Separator(new Dimension(40, 40));
                toolBarPanel.add(separator);
            }
            toolBarPanel.add(instrumentButton);
        }

        toolBar.add(toolBarPanel);
        frame.add(toolBar, BorderLayout.PAGE_START);
    }
}
