package ru.nsu.bolotov.gui.wireframe;

import ru.nsu.bolotov.gui.uicomponent.wireframe.WireframePanelInstrument;
import ru.nsu.bolotov.gui.uicomponent.wireframe.impl.Open;
import ru.nsu.bolotov.gui.uicomponent.wireframe.impl.ResetRotation;
import ru.nsu.bolotov.gui.uicomponent.wireframe.impl.Save;
import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_BUTTON_SIZE;

public class WireframeWindow {
    private final JFrame viewWireframeFrame;
    private final List<WireframePanelInstrument> wireframePanelInstruments = new ArrayList<>();

    public WireframeWindow(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        viewWireframeFrame = new JFrame("Wireframe Window");
        viewWireframeFrame.setMinimumSize(new Dimension(MINIMAL_WIDTH, MINIMAL_HEIGHT));
        viewWireframeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        WireframeViewPanel wireframeViewPanel = new WireframeViewPanel(bSplineRepresentation, applicationParameters);
        viewWireframeFrame.add(wireframeViewPanel);

        initializeInstruments(viewWireframeFrame, wireframeViewPanel);
        createToolBar(viewWireframeFrame);
        addMenuBar(viewWireframeFrame);

        viewWireframeFrame.pack();
        viewWireframeFrame.setLocationRelativeTo(null);
        viewWireframeFrame.setVisible(true);
    }

    private void initializeInstruments(JFrame frame, WireframeViewPanel wireframeViewPanel) {
        Save saveInstrument = new Save();
        Open openInstrument = new Open();
        ResetRotation resetRotationInstrument = new ResetRotation();

        wireframePanelInstruments.add(saveInstrument);
        wireframePanelInstruments.add(openInstrument);
        wireframePanelInstruments.add(resetRotationInstrument);

        for (WireframePanelInstrument instrument : wireframePanelInstruments) {
            instrument.injectActionListeners(frame, wireframeViewPanel);
        }
    }

    private void addMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu instrumentMenu = new JMenu("Instrument");
        for (WireframePanelInstrument instrument : wireframePanelInstruments) {
            if ("Open".equals(instrument.getInstrumentName()) || "Save".equals(instrument.getInstrumentName())) {
                fileMenu.add(instrument.getMenuButton());
            } else {
                instrumentMenu.add(instrument.getMenuButton());
            }
        }
        menuBar.add(fileMenu);
        menuBar.add(instrumentMenu);
        frame.setJMenuBar(menuBar);
    }

    private void createToolBar(JFrame frame) {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();

        JToolBar toolBar = new JToolBar("Instruments");
        JPanel toolBarPanel = new JPanel();
        for (WireframePanelInstrument instrument : wireframePanelInstruments) {
            JButton instrumentButton = instrument.getInstrumentButton();
            URL buttonFileURL = currentClassLoader.getResource("icons/icon-" + instrument.getInstrumentName().toLowerCase() + ".png");
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
