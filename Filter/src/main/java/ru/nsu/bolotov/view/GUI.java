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
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.RED, 40));
        imagePanel.setVisible(true);

        scrollPane.createHorizontalScrollBar();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        mainFrame.add(scrollPane, BorderLayout.CENTER);
        initializeInstruments();

        addMenuBar(mainFrame, imagePanel);
        addToolBar(mainFrame);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    public void startGUI() {
        mainFrame.setVisible(true);
    }

    private void initializeInstruments() {
        OpenFile openFile = new OpenFile();
        SaveFile saveFile = new SaveFile();
        ChangeViewMode changeViewMode = new ChangeViewMode();
        WhiteAndBlack whiteAndBlack = new WhiteAndBlack();
        Negative negative = new Negative();
        GaussSmoothing gaussSmoothing = new GaussSmoothing();
        SharpnessIncrease sharpnessIncrease = new SharpnessIncrease();
        Embossing embossing = new Embossing();
        GammaCorrection gammaCorrection = new GammaCorrection();
        RobertsOperator robertsOperator = new RobertsOperator();
        SobelOperator sobelOperator = new SobelOperator();
        FloydSteinbergDithering floydSteinbergDithering = new FloydSteinbergDithering();
        OrderlyDithering orderlyDithering = new OrderlyDithering();

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

//        JMenu instrumentsBar = new JMenu("Instruments");
//        for (PaintInstrument instrument : instruments) {
//            instrumentsBar.add(instrument.getMenuBarButton());
//        }

        menuBar.add(fileBar);
//        menuBar.add(instrumentsBar);
        menuBar.add(helpBar);
        frame.setJMenuBar(menuBar);
    }

    private void addToolBar(JFrame frame) {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();

        JToolBar toolBar = new JToolBar("Instruments");
        JPanel toolBarPanel = new JPanel();

        for (Instrument instrument : instrumentList) {
            JButton instrumentButton = instrument.getButton();
            URL buttonFileURL = currentClassLoader.getResource("icons/icon-" + instrument.getInstrumentName().toLowerCase() + ".png");
            ImageIcon buttonIcon = new ImageIcon(Objects.requireNonNull(buttonFileURL));
            instrumentButton.setIcon(buttonIcon);
            instrumentButton.setPreferredSize(new Dimension(45, 45));
            instrumentButton.setBorderPainted(true);
            toolBarPanel.add(instrumentButton);
        }

        toolBar.add(toolBarPanel);
        frame.add(toolBar, BorderLayout.PAGE_START);
    }
}
