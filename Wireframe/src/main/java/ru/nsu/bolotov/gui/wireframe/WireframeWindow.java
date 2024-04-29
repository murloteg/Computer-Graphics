package ru.nsu.bolotov.gui.wireframe;

import ru.nsu.bolotov.model.BSplineRepresentation;
import ru.nsu.bolotov.model.parameters.ApplicationParameters;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.*;
import static ru.nsu.bolotov.util.UtilConsts.DimensionConsts.STANDARD_BUTTON_SIZE;

public class WireframeWindow {
    private final JFrame viewWireframeFrame;

    public WireframeWindow(BSplineRepresentation bSplineRepresentation, ApplicationParameters applicationParameters) {
        viewWireframeFrame = new JFrame("Wireframe Window");
        viewWireframeFrame.setMinimumSize(new Dimension(MINIMAL_WIDTH, MINIMAL_HEIGHT));
        viewWireframeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        WireframeViewPanel wireframeViewPanel = new WireframeViewPanel(bSplineRepresentation, applicationParameters);
        viewWireframeFrame.add(wireframeViewPanel);
        createToolBar(viewWireframeFrame, wireframeViewPanel);

        viewWireframeFrame.pack();
        viewWireframeFrame.setLocationRelativeTo(null);
        viewWireframeFrame.setVisible(true);
    }

    private void createToolBar(JFrame frame, WireframeViewPanel wireframeViewPanel) {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();

        JToolBar toolBar = new JToolBar("Instruments");
        JPanel toolBarPanel = new JPanel();
        JButton resetRotationMatrixButton = new JButton();
        resetRotationMatrixButton.setToolTipText("Reset rotation");
        URL buttonFileURL = currentClassLoader.getResource("icons/icon-reset-rotate.png");
        ImageIcon buttonIcon = new ImageIcon(Objects.requireNonNull(buttonFileURL));
        resetRotationMatrixButton.setIcon(buttonIcon);
        resetRotationMatrixButton.setPreferredSize(new Dimension(STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE));
        resetRotationMatrixButton.setBorderPainted(true);
        resetRotationMatrixButton.addActionListener(event -> {
            wireframeViewPanel.resetRotationMatrix();
        });
        toolBarPanel.add(resetRotationMatrixButton);

        toolBar.add(toolBarPanel);
        frame.add(toolBar, BorderLayout.PAGE_START);
    }
}
