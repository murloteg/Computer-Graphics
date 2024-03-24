package ru.nsu.bolotov.view.imagepanel;

import ru.nsu.bolotov.exception.FailedLoadImage;
import ru.nsu.bolotov.exception.FailedSaveImage;
import ru.nsu.bolotov.model.filter.matrices.FilterMatrices;
import ru.nsu.bolotov.model.uicomponent.instrument.Instrument;
import ru.nsu.bolotov.model.filter.mode.FilterMode;
import ru.nsu.bolotov.util.UtilConsts;
import ru.nsu.bolotov.view.mode.ViewMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Dimension panelSize;
    private final JScrollPane spIm;
    private transient BufferedImage originImage = null;
    private transient BufferedImage changedImage;
    private transient BufferedImage currentViewImage;
    private Dimension imSize = null;
    private int lastX = 0;
    private int lastY = 0;
    private FilterMode filterMode = FilterMode.IDENTITY;
    private ViewMode viewMode = ViewMode.REAL_SIZE;
    private transient Instrument currentInstrument;
    private final Map<String, List<?>> applicationParameters = new HashMap<>();
    private final Random generator = new Random();
    private static final double ZOOM_COEFFICIENT = 0.05;

    /**
     * Creates default Image-viewer in the given JScrollPane.
     * Visible space will be painted in black.
     *
     * @param scrollPane - JScrollPane to add a new Image-viewer
     */
    public ImagePanel(JScrollPane scrollPane) {
        if (Objects.isNull(scrollPane)) {
            throw new IllegalArgumentException("ScrollPane wasn't specified");
        }

        spIm = scrollPane;
        spIm.setWheelScrollingEnabled(false);
        spIm.setDoubleBuffered(true);
        spIm.setViewportView(this);

        panelSize = getVisibleRectSize();
        spIm.validate();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (Objects.isNull(currentViewImage)) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else
            if (ViewMode.REAL_SIZE.equals(viewMode)) {
                g.drawImage(currentViewImage, 0, 0, null);
            } else {
                g.drawImage(currentViewImage, 0, 0, panelSize.width, panelSize.height, null);
            }
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public void updateImageOnPanel() {
        setImage(currentViewImage);
    }

    public void loadFileContent(File file) {
        try {
            BufferedImage openedImage = ImageIO.read(file);
            setImage(openedImage);
            originImage = openedImage;
            currentViewImage = originImage;

            changedImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), originImage.getType());
            Graphics2D g2d = changedImage.createGraphics();
            g2d.drawImage(currentViewImage, 0, 0, null);
            g2d.dispose();
        } catch (IOException exception) {
            throw new FailedLoadImage(exception);
        }
        SwingUtilities.updateComponentTreeUI(this.getParent());
    }

    public void saveCanvasContent(File file) {
        try {
            ImageIO.write(currentViewImage, "png", file);
        } catch (IOException exception) {
            throw new FailedSaveImage(exception);
        }
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public void startImageProcessing() {
        if (Objects.nonNull(originImage)) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            applyFilter();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Sets a new image to view.
     * If the given image is null, visible space will be painted in black.
     * Default view causes to "fit-screen" view.
     * If defaultView is set to false, viewer will show the last viewed part of the previous image.
     * But only if both of the images have the same size.
     *
     * @param newIm       - new image to view
     */
    private void setImage(BufferedImage newIm) {
        // defaultView means "fit screen (panel)"

        // Draw black screen for no image
        currentViewImage = newIm;
        if (Objects.isNull(currentViewImage)) {
            setMaxVisibleRectSize();
            repaint();
            revalidate();
            return;
        }

        imSize = new Dimension(currentViewImage.getWidth(), currentViewImage.getHeight());
        setMaxVisibleRectSize();
        double kh = (double) imSize.height / panelSize.height;
        double kw = (double) imSize.width / panelSize.width;
        double k = Math.max(kh, kw);

        panelSize.width = (int) (imSize.width / k);
        panelSize.height = (int) (imSize.height / k);

        spIm.getViewport().setViewPosition(new Point(0, 0));
        revalidate();
        spIm.paintAll(spIm.getGraphics());
        SwingUtilities.updateComponentTreeUI(this.getParent());
    }

    /**
     * @return Dimension object with the current view-size
     */
    private Dimension getVisibleRectSize() {
        // maximum size for panel with or without scrolling (inner border of the ScrollPane)
        Dimension viewportSize = spIm.getViewport().getSize();
        if (viewportSize.height == 0)
            return new Dimension(spIm.getWidth() - 3, spIm.getHeight() - 3);
        else
            return viewportSize;
    }

    /**
     * Sets panelSize to the maximum avaible view-size with hidden scroll bars.
     */
    private void setMaxVisibleRectSize() {
        // maximum size for panel without scrolling (inner border of the ScrollPane)
        panelSize = getVisibleRectSize();    // max size, but possibly with enabled scroll-bars
        revalidate();
        spIm.validate();
        panelSize = getVisibleRectSize();    // max size, without enabled scroll-bars
        revalidate();
    }

    private void setView(Rectangle rect) {
        setView(rect, 10);
    }

    private boolean setView(Rectangle rect, int minSize) {
        // should also take into account ScrollBars size
        if (originImage == null)
            return false;
        if (imSize.width < minSize || imSize.height < minSize)
            return false;

        if (minSize <= 0)
            minSize = 10;

        if (rect.width < minSize) rect.width = minSize;
        if (rect.height < minSize) rect.height = minSize;
        if (rect.x < 0) rect.x = 0;
        if (rect.y < 0) rect.y = 0;
        if (rect.x > imSize.width - minSize) rect.x = imSize.width - minSize;
        if (rect.y > imSize.height - minSize) rect.y = imSize.height - minSize;
        if ((rect.x + rect.width) > imSize.width) rect.width = imSize.width - rect.x;
        if ((rect.y + rect.height) > imSize.height) rect.height = imSize.height - rect.y;

        Dimension viewSize = getVisibleRectSize();
        double kw = (double) rect.width / viewSize.width;
        double kh = (double) rect.height / viewSize.height;
        double k = Math.max(kh, kw);

        int newPW = (int) (imSize.width / k);
        int newPH = (int) (imSize.height / k);
        // Check for size whether we can still zoom out
        if (newPW == (int) (newPW * (1 - 2 * ZOOM_COEFFICIENT)))
            return setView(rect, minSize * 2);
        panelSize.width = newPW;
        panelSize.height = newPH;

        revalidate();
        spIm.validate();

        int xc = rect.x + rect.width / 2;
        int yc = rect.y + rect.height / 2;
        xc = (int) (xc / k);
        yc = (int) (yc / k);
        spIm.getViewport().setViewPosition(new Point(xc - viewSize.width / 2, yc - viewSize.height / 2));
        revalidate();
        spIm.paintAll(spIm.getGraphics());

        return true;
    }

    public void initializeDefaultApplicationState(Map<String, List<?>> defaultParameters) {
        applicationParameters.putAll(defaultParameters);
    }

    public void setCurrentInstrument(Instrument currentInstrument) {
        this.currentInstrument = currentInstrument;
    }

    public java.util.List<?> getPreviousParametersForInstrument(String instrumentName) {
        return applicationParameters.get(instrumentName);
    }

    public void addStateToApplicationParameters(String instrumentName, java.util.List<?> parameters) {
        applicationParameters.put(instrumentName, parameters);
    }

    @Override
    public Dimension getPreferredSize() {
        return panelSize;
    }

    /**
     * Change zoom when scrolling
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (Objects.isNull(originImage) || ViewMode.REAL_SIZE.equals(viewMode)) {
            return;
        }

        double k = 1 - e.getWheelRotation() * ZOOM_COEFFICIENT;
        int newPW = (int) (panelSize.width * k);
        if (newPW == (int) (newPW * (1 + ZOOM_COEFFICIENT))) {
            return;
        }

        if (k > 1) {
            int newPH = (int) (panelSize.height * k);
            Dimension viewSize = getVisibleRectSize();
            int pixSizeX = newPW / imSize.width;
            int pixSizeY = newPH / imSize.height;
            if (pixSizeX > 0 && pixSizeY > 0) {
                int pixNumX = viewSize.width / pixSizeX;
                int pixNumY = viewSize.height / pixSizeY;
                if (pixNumX < 2 || pixNumY < 2)
                    return;
            }
        }

        panelSize.width = newPW;
        panelSize.height = (int) ((long) panelSize.width * imSize.height / imSize.width);

        int x = (int) (e.getX() * k);
        int y = (int) (e.getY() * k);
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x -= e.getX();
        scroll.y -= e.getY();
        scroll.x += x;
        scroll.y += y;

        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        spIm.repaint();
        SwingUtilities.updateComponentTreeUI(this.getParent());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    /**
     * Move visible image part when dragging
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK) {
            return;
        }

        if (ViewMode.REAL_SIZE.equals(viewMode)) {
            panelSize.width = imSize.width;
            panelSize.height = imSize.height;
        }

        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x += (lastX - e.getX());
        scroll.y += (lastY - e.getY());


        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        SwingUtilities.updateComponentTreeUI(this.getParent());
    }

    /**
     * When a rectangle is selected with pressed right button,
     * we zoom image to that rectangle
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getModifiersEx() != InputEvent.BUTTON3_DOWN_MASK)
            return;

        int x1 = e.getX();
        int y1 = e.getY();
        // Исключаем клик
        if (Math.abs(x1 - lastX) < 5 && Math.abs(y1 - lastY) < 5)
            return;

        double k = (double) imSize.width / panelSize.width;

        int x0 = (int) (k * lastX);
        int y0 = (int) (k * lastY);
        x1 = (int) (k * x1);
        y1 = (int) (k * y1);

        int w = Math.abs(x1 - x0);
        int h = Math.abs(y1 - y0);
        if (x1 < x0) x0 = x1;
        if (y1 < y0) y0 = y1;

        Rectangle rect = new Rectangle(x0, y0, w, h);
        setView(rect);
    }

    public void changeViewImage() {
        if (currentViewImage.equals(originImage)) {
            setImage(changedImage);
        } else if (currentViewImage.equals(changedImage)) {
            setImage(originImage);
        }
    }

    /**
     * Process image click and call parrent's methods
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && Objects.nonNull(imSize)) {
            changeViewImage();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void applyFilter() {
        String currentInstrumentName = currentInstrument.getInstrumentName();
        switch (filterMode) {
            case IDENTITY -> {
                return;
            }
            case WHITE_AND_BLACK -> {
                whiteAndBlackFilter();
            }
            case NEGATIVE -> {
                negativeFilter();
            }
            case EMBOSSING -> {
                int[][] matrix = FilterMatrices.EMBOSSING_MATRIX;
                applyMatrixFilter(matrix);
                postProcessingEmbossing();
            }
            case SHARPNESS_INCREASING -> {
                int[][] matrix = FilterMatrices.SHARPEN_MATRIX;
                applyMatrixFilter(matrix);
            }
            case GAUSS_SMOOTHING -> {
                int matrixSize = (int) applicationParameters.get(currentInstrumentName).get(0);
                int[][] matrix = null;
                if (matrixSize == 3) {
                    matrix = FilterMatrices.GAUSS_3X3_MATRIX;
                } else if (matrixSize == 5) {
                    matrix = FilterMatrices.GAUSS_5X5_MATRIX;
                } else if (matrixSize >= 7 && matrixSize % 2 != 0) {
                    medianFilter(matrixSize);
                    break;
                }
                applyMatrixFilter(matrix);
            }
            case GAMMA_CORRECTION -> {
                double gamma = (double) applicationParameters.get(currentInstrumentName).get(0);
                gammaCorrection(gamma);
            }
            case ROBERTS_OPERATOR -> {
                int[][] horizontalMatrix = FilterMatrices.ROBERTS_HORIZONTAL_MATRIX;
                int[][] verticalMatrix = FilterMatrices.ROBERTS_VERTICAL_MATRIX;
                double threshold = (double) applicationParameters.get(currentInstrumentName).get(0);
                twoDimensionalEdgeDetectionFilter(horizontalMatrix, verticalMatrix, threshold);
            }
            case SOBEL_OPERATOR -> {
                int[][] horizontalMatrix = FilterMatrices.SOBEL_HORIZONTAL_MATRIX;
                int[][] verticalMatrix = FilterMatrices.SOBEL_VERTICAL_MATRIX;
                double threshold = (double) applicationParameters.get(currentInstrumentName).get(0);
                twoDimensionalEdgeDetectionFilter(horizontalMatrix, verticalMatrix, threshold);
            }
            case FLOYD_STEINBERG_DITHERING -> {
                int redQuantization = (int) applicationParameters.get(currentInstrumentName).get(0);
                int greenQuantization = (int) applicationParameters.get(currentInstrumentName).get(0);
                int blueQuantization = (int) applicationParameters.get(currentInstrumentName).get(0);
                floydSteinbergDithering(redQuantization, greenQuantization, blueQuantization);
            }
            case ORDERLY_DITHERING -> {
                int redQuantization = (int) applicationParameters.get(currentInstrumentName).get(0);
                int greenQuantization = (int) applicationParameters.get(currentInstrumentName).get(0);
                int blueQuantization = (int) applicationParameters.get(currentInstrumentName).get(0);
                orderlyDithering(redQuantization, greenQuantization, blueQuantization);
            }
            case AQUA_REALIZATION -> {
                medianFilter(5);
                int[][] sharpenMatrix = FilterMatrices.SHARPEN_MATRIX;
                applyMatrixFilter(changedImage, sharpenMatrix);
            }
            case ROTATE -> {
                int angle = (int) applicationParameters.get(currentInstrumentName).get(0);
                rotate(angle);
            }
            case RETRO_EFFECT -> {
                int noiseLimit = (int) applicationParameters.get(currentInstrumentName).get(0);
                addNoise(noiseLimit);
                int[][] matrixFilter = FilterMatrices.GAUSS_5X5_MATRIX;
                applyMatrixFilter(changedImage, matrixFilter);
                retroEffectFilter(changedImage);
            }
        }
        currentViewImage = changedImage;
        setImage(currentViewImage);
    }

    private int clampColorComponent(int colorComponent) {
        return (colorComponent < 0) ? 0 : Math.min(colorComponent, 255);
    }

    private void postProcessingEmbossing() {
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = changedImage.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                int newRedComponent = clampColorComponent(redComponent + 128);
                int newGreenComponent = clampColorComponent(greenComponent + 128);
                int newBlueComponent = clampColorComponent(blueComponent + 128);
                int updatedColor = (newBlueComponent) | ((newGreenComponent) << 8) | ((newRedComponent) << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private void addNoise(int noiseLimit) {
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = originImage.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                redComponent += generator.nextInt(-noiseLimit, noiseLimit + 1);
                greenComponent += generator.nextInt(-noiseLimit, noiseLimit + 1);
                blueComponent += generator.nextInt(-noiseLimit, noiseLimit + 1);

                redComponent = clampColorComponent(redComponent);
                greenComponent = clampColorComponent(greenComponent);
                blueComponent = clampColorComponent(blueComponent);

                int updatedColor = blueComponent | (greenComponent << 8) | (redComponent << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private int getMiddleColorForPixel(int pixelColor) {
        int redComponent = (pixelColor >> 16) & 0xFF;
        int greenComponent = (pixelColor >> 8) & 0xFF;
        int blueComponent = pixelColor & 0xFF;

        double redCoefficient = 0.299;
        double greenCoefficient = 0.587;
        double blueCoefficient = 0.114;
        return (int) ((redCoefficient * redComponent + greenCoefficient * greenComponent + blueCoefficient * blueComponent) / 3);
    }

    private void whiteAndBlackFilter() {
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = originImage.getRGB(x, y);
                int middleColor = getMiddleColorForPixel(pixelColor);
                int updatedColor = middleColor | (middleColor << 8) | (middleColor << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private void negativeFilter() {
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = originImage.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                int updatedColor = (255 - blueComponent) | ((255 - greenComponent) << 8) | ((255 - redComponent) << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private void gammaCorrection(double gamma) {
        double gammaCorrection = 1.0 / gamma;
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = originImage.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                int newRedComponent = Math.min((int) (255 * (Math.pow(redComponent / 255.0, gammaCorrection))), 255);
                int newGreenComponent = Math.min((int) (255 * (Math.pow(greenComponent / 255.0, gammaCorrection))), 255);
                int newBlueComponent = Math.min((int) (255 * (Math.pow(blueComponent / 255.0, gammaCorrection))), 255);
                int updatedColor = newBlueComponent | (newGreenComponent << 8) | (newRedComponent << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private double[][] generateRotationMatrix2D(double sin, double cos) {
        return new double[][]{
                {cos, (-1) * sin},
                {sin, cos}
        };
    }

    private int[] transformCoordinates(double[][] rotationMatrix, int x, int y) {
        int rotatedX = (int) Math.round(rotationMatrix[0][0] * x + rotationMatrix[0][1] * y);
        int rotatedY = (int) Math.round(rotationMatrix[1][0] * x + rotationMatrix[1][1] * y);
        return new int[]{rotatedX, rotatedY};
    }

    private void rotate(int angleInDegrees) {
        double angleSin = Math.sin(Math.toRadians(angleInDegrees));
        double angleCos = Math.cos(Math.toRadians(angleInDegrees));
        double[][] rotationMatrix = generateRotationMatrix2D(angleSin, angleCos);

        int width = originImage.getWidth();
        int height = originImage.getHeight();

        int newWidth = width;
        int newHeight = height;
        if (angleInDegrees % 180 != 0) {
            newWidth = (int) (Math.abs(width * angleCos) + Math.abs(height * angleSin));
            newHeight = (int) (Math.abs(width * angleSin) + Math.abs(height * angleCos));
        }

        changedImage = new BufferedImage(newWidth, newHeight, originImage.getType());
        changedImage.getGraphics().fillRect(0, 0, newWidth, newHeight);

        int centerX = newWidth / 2;
        int centerY = newHeight / 2;

        int originalCenterX = width / 2;
        int originalCenterY = height / 2;
        int[] rotatedCenter = transformCoordinates(rotationMatrix, originalCenterX, originalCenterY);
        int rotatedCenterX = rotatedCenter[0];
        int rotatedCenterY = rotatedCenter[1];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int[] rotatedCoordinates = transformCoordinates(rotationMatrix, x, y);
                int rotatedX = rotatedCoordinates[0] + centerX - rotatedCenterX;
                int rotatedY = rotatedCoordinates[1] + centerY - rotatedCenterY;

                if (rotatedX >= 0 && rotatedX < changedImage.getWidth() && rotatedY >= 0 && rotatedY < changedImage.getHeight()) {
                    int pixelColor = originImage.getRGB(x, y);
                    changedImage.setRGB(rotatedX, rotatedY, pixelColor);
                }
            }
        }
    }

    private void retroEffectFilter(BufferedImage source) {
        for (int x = 0; x < source.getWidth(); ++x) {
            for (int y = 0; y < source.getHeight(); ++y) {
                int pixelColor = source.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                redComponent = (int) Math.round((redComponent * 0.393) + (greenComponent * 0.769) + (blueComponent * 0.189));
                greenComponent = (int) Math.round((redComponent * 0.349) + (greenComponent * 0.686) + (blueComponent * 0.168));
                blueComponent = (int) Math.round((redComponent * 0.272) + (greenComponent * 0.584) + (blueComponent * 0.131));

                redComponent = clampColorComponent(redComponent);
                greenComponent = clampColorComponent(greenComponent);
                blueComponent = clampColorComponent(blueComponent);

                int updatedColor = blueComponent | (greenComponent << 8) | (redComponent << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private boolean isBorderedValue(int position, int width, int height, int shiftFromBorderInPx) {
        return position < shiftFromBorderInPx * width || position >= (width * (height - shiftFromBorderInPx))
                || position % width < shiftFromBorderInPx || position % width >= (width - shiftFromBorderInPx);
    }

    private double calculatePaletteGroupCoefficient(int oldColor, int quantization) {
        return oldColor * quantization / 255.0;
    }

    private int findRightPaletteNeighborColor(int oldColor, int quantization) {
        int paletteGroupCoefficient = (int) Math.round(calculatePaletteGroupCoefficient(oldColor, quantization));
        int step = (int) Math.round(255.0 / quantization);
        int rightNeighborCoefficient = Math.max(paletteGroupCoefficient + 1, quantization - 1);
        return Math.min(step * rightNeighborCoefficient, 255);
    }

    private int findClosestPaletteColor(int oldColor, int quantization) {
        int paletteGroupCoefficient = (int) Math.round(calculatePaletteGroupCoefficient(oldColor, quantization));
        int step = (int) Math.round(255.0 / quantization);
        return Math.min(step * paletteGroupCoefficient, 255);
    }

    private void fillRGBBuffers(double[][] redBuffer, double[][] greenBuffer, double[][] blueBuffer) {
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = originImage.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                redBuffer[y][x] = redComponent;
                greenBuffer[y][x] = greenComponent;
                blueBuffer[y][x] = blueComponent;
            }
        }
    }

    private void floydSteinbergErrorPropagation(double[][] colorBuffer, int x, int y, int quantization) {
        double component = colorBuffer[y][x];
        int newComponent = findClosestPaletteColor((int) Math.round(component), quantization);
        double error = component - newComponent;
        colorBuffer[y][x + 1] += error * 7 / 16;
        colorBuffer[y + 1][x - 1] += error * 3 / 16;
        colorBuffer[y + 1][x] += error * 5 / 16;
        colorBuffer[y + 1][x + 1] += error * 1 / 16;
    }

    private void floydSteinbergDithering(int redQuantization, int greenQuantization, int blueQuantization) {
        int width = originImage.getWidth();
        int height = originImage.getHeight();
        double[][] redBuffer = new double[height][width];
        double[][] greenBuffer = new double[height][width];
        double[][] blueBuffer = new double[height][width];
        fillRGBBuffers(redBuffer, greenBuffer, blueBuffer);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int position = y * width + x;
                if (isBorderedValue(position, width, height, 1)) {
                    continue;
                }
                floydSteinbergErrorPropagation(redBuffer, x, y, redQuantization);
                floydSteinbergErrorPropagation(greenBuffer, x, y, greenQuantization);
                floydSteinbergErrorPropagation(blueBuffer, x, y, blueQuantization);
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int updatedColor = clampColorComponent((int) blueBuffer[y][x])
                        | clampColorComponent((int) (greenBuffer[y][x])) << 8
                        | clampColorComponent((int) (redBuffer[y][x])) << 16;
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private int[][] generateOrderlyDitheringMatrixWithDegreeOfTwoSize(int degreeOfTwo) {
        int[][] matrix = FilterMatrices.ORDERLY_DITHERING_BASE_MATRIX;
        for (int i = 0; i < (degreeOfTwo - 1); ++i) {
            int matrixSize = matrix.length;
            int[][] nextMatrix = new int[2 * matrixSize][2 * matrixSize];
            for (int x = 0; x < matrixSize; ++x) {
                for (int y = 0; y < matrixSize; ++y) {
                    int value = matrix[y][x];
                    int multiplier = 4;
                    nextMatrix[y][x] = multiplier * value;
                    nextMatrix[y][x + matrixSize] = multiplier * value + 2;
                    nextMatrix[y + matrixSize][x] = multiplier * value + 3;
                    nextMatrix[y + matrixSize][x + matrixSize] = multiplier * value + 1;
                }
            }
            matrix = nextMatrix;
        }
        return matrix;
    }

    private int calculateSuitableDegreeForDitheringMatrixByQuantization(int quantization) {
        return (int) Math.round(Math.log(quantization) / Math.log(2));
    }

    private double calculateDivisorForDitheringMatrix(int degreeOfTwo) {
        return Math.pow(4, degreeOfTwo - 1.0);
    }

    private int determinateColorComponentByDitheringMatrix(int[][] ditheringMatrix, int colorComponent, int x, int y,
                                                           int quantization, int degree) {
        double divisor = calculateDivisorForDitheringMatrix(degree);
        int matrixSize = ditheringMatrix.length;
        int newComponent = (int) (ditheringMatrix[y % matrixSize][x % matrixSize] * 256 / divisor);
        return (colorComponent <= newComponent) ? findClosestPaletteColor(colorComponent, quantization) :
                findRightPaletteNeighborColor(colorComponent, quantization);
    }

    private void orderlyDithering(int redQuantization, int greenQuantization, int blueQuantization) {
        int redDegree = calculateSuitableDegreeForDitheringMatrixByQuantization(redQuantization);
        int greenDegree = calculateSuitableDegreeForDitheringMatrixByQuantization(greenQuantization);
        int blueDegree = calculateSuitableDegreeForDitheringMatrixByQuantization(blueQuantization);

        int[][] redDitheringMatrix = generateOrderlyDitheringMatrixWithDegreeOfTwoSize(redDegree);
        int[][] greenDitheringMatrix = generateOrderlyDitheringMatrixWithDegreeOfTwoSize(greenDegree);
        int[][] blueDitheringMatrix = generateOrderlyDitheringMatrixWithDegreeOfTwoSize(blueDegree);
        for (int x = 0; x < originImage.getWidth(); ++x) {
            for (int y = 0; y < originImage.getHeight(); ++y) {
                int pixelColor = originImage.getRGB(x, y);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                redComponent = determinateColorComponentByDitheringMatrix(redDitheringMatrix, redComponent, x, y, redQuantization, redDegree);
                greenComponent = determinateColorComponentByDitheringMatrix(greenDitheringMatrix, greenComponent, x, y, greenQuantization, greenDegree);
                blueComponent = determinateColorComponentByDitheringMatrix(blueDitheringMatrix, blueComponent, x, y, blueQuantization, blueDegree);

                int updatedColor = blueComponent | (greenComponent << 8) | (redComponent << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private int calculateMedianValue(int x, int y, int matrixSize, java.util.List<Integer> pixels) {
        int matrixRadius = matrixSize / 2;

        for (int i = 0; i < matrixSize; ++i) {
            for (int j = 0; j < matrixSize; ++j) {
                int currentY = Math.min(Math.max(y + i - matrixRadius, 0), originImage.getHeight() - 1);
                int currentX = Math.min(Math.max(x + j - matrixRadius, 0), originImage.getWidth() - 1);

                int pixelColor = originImage.getRGB(currentX, currentY);
                pixels.add(pixelColor);
            }
        }
        pixels.sort(Comparator.naturalOrder());
        int medianValue = pixels.get(matrixSize / 2);
        pixels.clear();
        return medianValue;
    }

    private void medianFilter(int matrixSize) {
        int imageWidth = originImage.getWidth();
        int imageHeight = originImage.getHeight();
        int shiftFromBorderInPx = matrixSize / 2;

        ExecutorService executorService = Executors.newFixedThreadPool(UtilConsts.ProgramConfigurationConsts.THREADS_NUMBERS);
        int stepY = imageHeight / UtilConsts.ProgramConfigurationConsts.THREADS_NUMBERS;
        java.util.List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        for (int i = 0; i < UtilConsts.ProgramConfigurationConsts.THREADS_NUMBERS; ++i) {
            final int futureIndex = i;
            completableFutures.add(CompletableFuture.runAsync(() -> {
                java.util.List<Integer> pixels = new ArrayList<>();
                int startY = futureIndex * stepY;
                int endY = (futureIndex + 1) * stepY;
                for (int x = 0; x < imageWidth; ++x) {
                    for (int y = startY; y < endY; ++y) {
                        int position = y * imageWidth + x;
                        if (isBorderedValue(position, imageWidth, imageHeight, shiftFromBorderInPx)) {
                            continue;
                        }
                        int updatedColor = calculateMedianValue(x, y, matrixSize, pixels);
                        changedImage.setRGB(x, y, updatedColor);
                    }
                }
            }, executorService));
        }
        completableFutures.forEach(CompletableFuture::join);
    }

    private int calculateDivisorForMatrix(int[][] filterMatrix) {
        int divisor = 0;
        int matrixRows = filterMatrix.length;
        for (int[] matrix : filterMatrix) {
            for (int j = 0; j < matrixRows; ++j) {
                divisor += matrix[j];
            }
        }
        return (divisor == 0) ? 1 : divisor;
    }

    private int calculateMultiplicationResultForFilterMatrix(BufferedImage sourceImage, int[][] filterMatrix, int x, int y, int divisor) {
        int matrixRows = filterMatrix.length;
        int matrixRadius = matrixRows / 2;

        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        for (int i = 0; i < matrixRows; ++i) {
            for (int j = 0; j < matrixRows; ++j) {
                int currentY = Math.min(Math.max(y + i - matrixRadius, 0), sourceImage.getHeight() - 1);
                int currentX = Math.min(Math.max(x + j - matrixRadius, 0), sourceImage.getWidth() - 1);

                int pixelColor = sourceImage.getRGB(currentX, currentY);
                int redComponent = (pixelColor >> 16) & 0xFF;
                int greenComponent = (pixelColor >> 8) & 0xFF;
                int blueComponent = pixelColor & 0xFF;

                redSum += redComponent * filterMatrix[i][j];
                greenSum += greenComponent * filterMatrix[i][j];
                blueSum += blueComponent * filterMatrix[i][j];
            }
        }

        redSum /= divisor;
        greenSum /= divisor;
        blueSum /= divisor;

        redSum = clampColorComponent(redSum);
        greenSum = clampColorComponent(greenSum);
        blueSum = clampColorComponent(blueSum);
        return blueSum | (greenSum << 8) | (redSum << 16);
    }

    private double convertColorToZeroOneSection(int color) {
        int redComponent = (color >> 16) & 0xFF;
        int greenComponent = (color >> 8) & 0xFF;
        int blueComponent = color & 0xFF;
        return 1 - ((1.0 / (redComponent + 1) + 1.0 / (greenComponent + 1) + 1.0 / (blueComponent + 1)) / 3);
    }

    private void twoDimensionalEdgeDetectionFilter(int[][] horizontalMatrix, int[][] verticalMatrix, double threshold) {
        whiteAndBlackFilter();
        BufferedImage grayscaleImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), originImage.getType());
        changedImage.copyData(grayscaleImage.getRaster());
        int imageWidth = originImage.getWidth();
        int imageHeight = originImage.getHeight();
        int shiftFromBorderInPx = verticalMatrix.length / 2;
        for (int x = 0; x < imageWidth; ++x) {
            for (int y = 0; y < imageHeight; ++y) {
                int position = y * imageWidth + x;
                if (isBorderedValue(position, imageWidth, imageHeight, shiftFromBorderInPx)) {
                    continue;
                }
                int verticalColor = calculateMultiplicationResultForFilterMatrix(grayscaleImage, verticalMatrix, x, y, 1);
                int horizontalColor = calculateMultiplicationResultForFilterMatrix(grayscaleImage, horizontalMatrix, x, y, 1);
                int result = Math.abs(verticalColor) + Math.abs(horizontalColor);
                double mappedValue = convertColorToZeroOneSection(result);
                changedImage.setRGB(x, y, mappedValue >= threshold ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
    }

    private void applyMatrixFilter(int[][] filterMatrix) {
        int imageWidth = originImage.getWidth();
        int imageHeight = originImage.getHeight();
        int divisor = calculateDivisorForMatrix(filterMatrix);
        int shiftFromBorderInPx = filterMatrix.length / 2;
        for (int x = 0; x < imageWidth; ++x) {
            for (int y = 0; y < imageHeight; ++y) {
                int position = y * imageWidth + x;
                if (isBorderedValue(position, imageWidth, imageHeight, shiftFromBorderInPx)) {
                    continue;
                }
                int updatedColor = calculateMultiplicationResultForFilterMatrix(originImage, filterMatrix, x, y, divisor);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    private void applyMatrixFilter(BufferedImage source, int[][] filterMatrix) {
        BufferedImage temporaryImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), originImage.getType());
        changedImage.copyData(temporaryImage.getRaster());
        int imageWidth = source.getWidth();
        int imageHeight = source.getHeight();
        int divisor = calculateDivisorForMatrix(filterMatrix);
        int shiftFromBorderInPx = filterMatrix.length / 2;
        for (int x = 0; x < imageWidth; ++x) {
            for (int y = 0; y < imageHeight; ++y) {
                int position = y * imageWidth + x;
                if (isBorderedValue(position, imageWidth, imageHeight, shiftFromBorderInPx)) {
                    continue;
                }
                int updatedColor = calculateMultiplicationResultForFilterMatrix(temporaryImage, filterMatrix, x, y, divisor);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }
}
