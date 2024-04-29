package ru.nsu.bolotov.gui.bspline;

import ru.nsu.bolotov.gui.bspline.configuration.ParametersPanel;
import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;
import ru.nsu.bolotov.model.uicomponent.EditorInstrument;
import ru.nsu.bolotov.model.uicomponent.impl.*;

import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_BUTTON_SIZE;
import static ru.nsu.bolotov.util.UtilConsts.StringConsts.*;

public class BSplineEditorFrame {
    private final JFrame bSplineFrame;
    private final java.util.List<EditorInstrument> editorInstrumentList;

    public BSplineEditorFrame(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        bSplineFrame = new JFrame(BSPLINE_EDITOR_TITLE);
        editorInstrumentList = new ArrayList<>();
        initializeBSplineFrame(bSplineRepresentation, applicationParameters);
    }

    private void initializeBSplineFrame(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        bSplineFrame.setMinimumSize(new Dimension(MINIMAL_WIDTH, MINIMAL_HEIGHT));
        bSplineFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        EditorPanel editorPanel = new EditorPanel(bSplineRepresentation, applicationParameters);
        bSplineFrame.add(editorPanel, BorderLayout.CENTER);

        ParametersPanel parametersPanel = new ParametersPanel(applicationParameters, editorPanel);
        bSplineFrame.add(parametersPanel, BorderLayout.AFTER_LAST_LINE);

        initializeInstruments(bSplineFrame, editorPanel);
        addMenuBar(bSplineFrame, editorPanel);
        addToolBar(bSplineFrame);

        bSplineFrame.pack();
        bSplineFrame.setLocationRelativeTo(null);
        bSplineFrame.setVisible(true);
    }

    private void initializeInstruments(JFrame frame, EditorPanel editorPanel) {
        Save saveInstrument = new Save();
        Open openInstrument = new Open();
        CreatePoints createPointsInstrument = new CreatePoints();
        MovePlane movePlaneInstrument = new MovePlane();
        Normalize normalizeInstrument = new Normalize();
        Info infoInstrument = new Info();

        editorInstrumentList.add(saveInstrument);
        editorInstrumentList.add(openInstrument);
//        editorInstrumentList.add(createPointsInstrument);
//        editorInstrumentList.add(movePlaneInstrument);
        editorInstrumentList.add(normalizeInstrument);
        editorInstrumentList.add(infoInstrument);

        for (EditorInstrument editorInstrument : editorInstrumentList) {
            editorInstrument.injectActionListeners(frame, editorPanel);
        }
    }

    private JMenu createRunMenuBar(EditorPanel editorPanel) {
        JMenu runBar = new JMenu("Run");
        JMenuItem runMenuItem = new JMenuItem("Start processing");
        runMenuItem.addActionListener(event -> {
            editorPanel.updateBSpline();
        });
        runBar.add(runMenuItem);
        return runBar;
    }

    private void addMenuBar(JFrame frame, EditorPanel editorPanel) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu moveStateMenu = new JMenu("Move");
        JMenu instrumentMenu = new JMenu("Instrument");
        JMenu helpMenu = new JMenu("Help");
        for (EditorInstrument editorInstrument : editorInstrumentList) {
            if ("Open".equals(editorInstrument.getInstrumentName()) || "Save".equals(editorInstrument.getInstrumentName())) {
                fileMenu.add(editorInstrument.getMenuButton());
            } else if ("Info".equals(editorInstrument.getInstrumentName())) {
                helpMenu.add(editorInstrument.getMenuButton());
            } else if ("MovePlane".equals(editorInstrument.getInstrumentName()) || "CreatePoints".equals(editorInstrument.getInstrumentName())) {
                moveStateMenu.add(editorInstrument.getMenuButton());
            } else {
                instrumentMenu.add(editorInstrument.getMenuButton());
            }
        }
        JMenu runBar = createRunMenuBar(editorPanel);

        menuBar.add(fileMenu);
//        menuBar.add(moveStateMenu);
        menuBar.add(instrumentMenu);
        menuBar.add(helpMenu);
        menuBar.add(runBar);
        frame.setJMenuBar(menuBar);
    }

    private void addToolBar(JFrame frame) {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();

        JToolBar toolBar = new JToolBar("Instruments");
        JPanel toolBarPanel = new JPanel();

        for (EditorInstrument editorInstrument : editorInstrumentList) {
            JButton instrumentButton = editorInstrument.getInstrumentButton();
            URL buttonFileURL = currentClassLoader.getResource("icons/icon-" + editorInstrument.getInstrumentName().toLowerCase() + ".png");
            ImageIcon buttonIcon = new ImageIcon(Objects.requireNonNull(buttonFileURL));
            instrumentButton.setIcon(buttonIcon);
            instrumentButton.setPreferredSize(new Dimension(STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE));
            instrumentButton.setBorderPainted(true);
            toolBarPanel.add(instrumentButton);
        }
        toolBar.add(toolBarPanel);
        frame.add(toolBar, BorderLayout.PAGE_START);
    }
}
