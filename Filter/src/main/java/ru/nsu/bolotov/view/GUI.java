package ru.nsu.bolotov.view;

import ru.nsu.bolotov.model.uicomponent.instrument.DialogEnabled;
import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.model.uicomponent.instrument.impl.*;
import ru.nsu.bolotov.view.imagepanel.ImagePanel;
import ru.nsu.bolotov.view.mode.interpolation.InterpolationMode;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.*;

import static ru.nsu.bolotov.util.UtilConsts.ButtonNameConsts.ABOUT_BUTTON;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.ABOUT_PROGRAM_TEXT;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.APPLICATION_TITLE;

public class GUI {
    private final JFrame mainFrame;
    private final java.util.List<Instrument> instrumentList = new ArrayList<>();

    public GUI() {
        mainFrame = new JFrame(APPLICATION_TITLE);

        mainFrame.setMinimumSize(new Dimension(MINIMAL_WEIGHT, MINIMAL_HEIGHT));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        ImagePanel imagePanel = new ImagePanel(scrollPane);
        imagePanel.setMinimumSize(new Dimension(IMAGE_PANEL_SIZE, IMAGE_PANEL_SIZE));
        imagePanel.setVisible(true);

        scrollPane.createHorizontalScrollBar();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

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
        addToolBar(mainFrame);

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

        Map<String, List<?>> defaultParameters = new HashMap<>();
        for (Instrument instrument : instrumentList) {
            instrument.injectActionListeners(instrumentList, buttonGroup);
            if (instrument instanceof DialogEnabled instrumentWithParameters) {
                defaultParameters.put(instrument.getInstrumentName(), instrumentWithParameters.getDefaultParameters());
            }
        }
        imagePanel.initializeDefaultApplicationState(defaultParameters);
        imagePanel.setCurrentInstrument(openFile);
    }

    private void addMenuBar(JFrame frame, ImagePanel imagePanel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileBar = new JMenu("File");
        JMenu runBar = new JMenu("Run");
        JMenu instrumentsBar = new JMenu("Instruments");
        for (Instrument instrument : instrumentList) {
            if (instrument.getInstrumentName().endsWith("File")) {
                fileBar.add(instrument.getMenuButton());
                continue;
            }
            if (instrument.getInstrumentName().endsWith("Processing")) {
                runBar.add(instrument.getMenuButton());
                continue;
            }
            instrumentsBar.add(instrument.getMenuButton());
        }
        JMenu viewBar = createViewBar(imagePanel);
        JMenu helpBar = createHelpBar(frame);
        JMenu interpolationBar = createInterpolationBar(imagePanel);

        menuBar.add(fileBar);
        menuBar.add(viewBar);
        menuBar.add(instrumentsBar);
        menuBar.add(runBar);
        menuBar.add(helpBar);

        JSeparator menuBarSeparator = new JSeparator();
        menuBar.add(menuBarSeparator);
        menuBar.add(interpolationBar);
        frame.setJMenuBar(menuBar);
    }

    private JMenu createHelpBar(JFrame frame) {
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
        return helpBar;
    }

    private JMenu createViewBar(ImagePanel imagePanel) {
        JMenuItem changeImageItem = new JMenuItem("Change displayed image");
        changeImageItem.addActionListener(event -> {
            imagePanel.changeViewImage();
        });

        JMenu viewBar = new JMenu("View");
        viewBar.add(changeImageItem);
        return viewBar;
    }

    private JMenu createInterpolationBar(ImagePanel imagePanel) {
        JMenu interpolationBar = new JMenu("Interpolation");
        ButtonGroup interpolationGroup = new ButtonGroup();

        JRadioButtonMenuItem bilinear = new JRadioButtonMenuItem("Bilinear");
        JRadioButtonMenuItem bicubic = new JRadioButtonMenuItem("Bicubic");
        JRadioButtonMenuItem nearestNeighbor = new JRadioButtonMenuItem("Nearest neighbor");

        interpolationGroup.add(bilinear);
        interpolationGroup.add(bicubic);
        interpolationGroup.add(nearestNeighbor);
        bilinear.setSelected(true);

        bilinear.addActionListener(event -> {
            imagePanel.setInterpolationMode(InterpolationMode.BILINEAR_INTERPOLATION);
        });

        bicubic.addActionListener(event -> {
            imagePanel.setInterpolationMode(InterpolationMode.BICUBIC_INTERPOLATION);
        });

        nearestNeighbor.addActionListener(event -> {
            imagePanel.setInterpolationMode(InterpolationMode.NEAREST_NEIGHBOR_INTERPOLATION);
        });

        interpolationBar.add(bilinear);
        interpolationBar.add(bicubic);
        interpolationBar.add(nearestNeighbor);
        return interpolationBar;
    }

    private void addToolBar(JFrame frame) {
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
                JSeparator separator = new JToolBar.Separator(new Dimension(STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE));
                toolBarPanel.add(separator);
            }
            toolBarPanel.add(instrumentButton);
        }
        toolBar.add(toolBarPanel);
        frame.add(toolBar, BorderLayout.PAGE_START);
    }
}
