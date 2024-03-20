package ru.nsu.bolotov.view.imagepanel;

import ru.nsu.bolotov.exception.FailedLoadImage;
import ru.nsu.bolotov.exception.FailedSaveImage;
import ru.nsu.bolotov.model.FilterMatrices;
import ru.nsu.bolotov.model.mode.FilterMode;
import ru.nsu.bolotov.util.UtilConsts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Dimension panelSize;        // visible image size
    private JScrollPane spIm;
    private JFrame parentComponent;
    private BufferedImage originImage = null;    // image to view
    private BufferedImage changedImage;
    private BufferedImage currentViewImage;
    private Dimension imSize = null;    // real image size
    private int lastX = 0, lastY = 0;        // last captured mouse coordinates
    private double zoomK = 0.05;        // scroll zoom coefficient
    private FilterMode filterMode = FilterMode.CHANGE_VIEW_MODE;
    private final Random generator = new Random();

    /**
     * Creates default Image-viewer in the given JScrollPane.
     * Visible space will be painted in black.
     *
     * @param scrollPane - JScrollPane to add a new Image-viewer
     * @throws Exception - given JScrollPane must not be null
     */
    public ImagePanel(JScrollPane scrollPane, JFrame parentComponent) throws RuntimeException {
        if (scrollPane == null)
            throw new RuntimeException("Отсутствует scroll panel для просмотрщика изображений!");

        spIm = scrollPane;
        spIm.setWheelScrollingEnabled(false);
        spIm.setDoubleBuffered(true);
        spIm.setViewportView(this);

        this.parentComponent = parentComponent;

        panelSize = getVisibleRectSize();    // adjust panel size to maximum visible in scrollPane
        spIm.validate();                    // added panel to scrollPane
        // setMaxVisibleRectSize();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public JScrollPane getScrollPane() {
        return spIm;
    }

    /**
     * Creates new Image-viewer of the given image in the given JScrollPane
     *
     * @param scrollPane - JScrollPane to add a new Image-viewer
     * @param newIm      - image to view
     * @throws Exception - given JScrollPane must nor be null
     */
    public ImagePanel(JScrollPane scrollPane, JFrame parentComponent, BufferedImage newIm) throws Exception {
        this(scrollPane, parentComponent);
        setImage(newIm, true);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (Objects.isNull(currentViewImage)) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else
            g.drawImage(currentViewImage, 0, 0, panelSize.width, panelSize.height, null);
//            g.drawImage(currentViewImage, 0, 0, null);
    }

    public void loadFileContent(File file, boolean isDefaultView) {
        try {
            BufferedImage openedImage = ImageIO.read(file);
            setImage(openedImage, isDefaultView);
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
            ImageIO.write(originImage, "png", file);
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
     * @param defaultView - view the image in the default view, or save the view on the image
     */
    public void setImage(BufferedImage newIm, boolean defaultView) {
        // defaultView means "fit screen (panel)"

        // Draw black screen for no image
        currentViewImage = newIm;
        if (currentViewImage == null) {
            // make full defaultView
            setMaxVisibleRectSize();    // panelSize = getVisibleRectSize();
            repaint();
            revalidate();    // spIm.validate();
            return;
        }

        imSize = new Dimension(currentViewImage.getWidth(), currentViewImage.getHeight());
        // Check if it is possible to use defaultView
        Dimension newImSize = new Dimension(currentViewImage.getWidth(), currentViewImage.getHeight());
        if (imSize == null)
            defaultView = true;
        else if ((newImSize.height != imSize.height) || (newImSize.width != imSize.width))
            defaultView = true;

        imSize = newImSize;

        if (defaultView) {
            setMaxVisibleRectSize();    // panelSize = getVisibleRectSize();

            double kh = (double) imSize.height / panelSize.height;
            double kw = (double) imSize.width / panelSize.width;
            double k = Math.max(kh, kw);

            panelSize.width = (int) (imSize.width / k);
            panelSize.height = (int) (imSize.height / k);
            //this.setSize(panelSize);

            //repaint();
            spIm.getViewport().setViewPosition(new Point(0, 0));
            //spIm.getHorizontalScrollBar().setValue(0);
            //spIm.getVerticalScrollBar().setValue(0);
            revalidate();    // spIm.validate();
            //spIm.repaint();	// wipe off the old picture in "spare" space
            spIm.paintAll(spIm.getGraphics());
        } else {
            // just change image
            //repaint();
            spIm.getViewport().setViewPosition(new Point(0, 0));
            spIm.paintAll(spIm.getGraphics());
        }
        SwingUtilities.updateComponentTreeUI(this.getParent());
    }

    /**
     * Sets "fit-screen" view.
     */
    public void fitScreen() {
        setImage(originImage, true);
    }

    /**
     * Sets "real-size" view.
     */
    public void realSize() {
        if (imSize == null)
            return;

        double k = (double) imSize.width / panelSize.width;
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x *= k;
        scroll.y *= k;

        panelSize.setSize(imSize);

        //repaint();
        revalidate();    // spIm.validate();
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        //spIm.repaint();
        spIm.paintAll(spIm.getGraphics());
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

    public boolean setView(Rectangle rect) {
        return setView(rect, 10);
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
        if (newPW == (int) (newPW * (1 - 2 * zoomK)))
            return setView(rect, minSize * 2);
        panelSize.width = newPW;
        panelSize.height = newPH;

        revalidate();
        spIm.validate();
        // сначала нужно, чтобы scroll понял новый размер, потом сдвигать

        int xc = rect.x + rect.width / 2, yc = rect.y + rect.height / 2;
        xc = (int) (xc / k);
        yc = (int) (yc / k);    // we need to center new view
        //int x0 = (int)(rect.x/k), y0 = (int)(rect.y/k);
        spIm.getViewport().setViewPosition(new Point(xc - viewSize.width / 2, yc - viewSize.height / 2));
        revalidate();    // spIm.validate();
        spIm.paintAll(spIm.getGraphics());

        return true;
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
        if (originImage == null)
            return;

        // Zoom
        double k = 1 - e.getWheelRotation() * zoomK;

        // Check for minimum size where we can still increase size
        int newPW = (int) (panelSize.width * k);
        if (newPW == (int) (newPW * (1 + zoomK)))
            return;
//		if (newW/imSize.width > 50)
//			return;
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
        // panelSize.height *= k;
        panelSize.height = (int) ((long) panelSize.width * imSize.height / imSize.width);    // not to loose ratio

        // Move so that mouse position doesn't visibly change
        int x = (int) (e.getX() * k);
        int y = (int) (e.getY() * k);
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x -= e.getX();
        scroll.y -= e.getY();
        scroll.x += x;
        scroll.y += y;

        SwingUtilities.updateComponentTreeUI(this.getParent());

        //spIm.getViewport().setViewPosition(scroll);	// так верхний левый угол может выйти за рамки изображения
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        spIm.repaint();
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
        // Move image with mouse

        if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK)        // ( (e.getModifiers() & MouseEvent.BUTTON3_MASK) == 0)
            return;

        // move picture using scroll
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x += (lastX - e.getX());
        scroll.y += (lastY - e.getY());

        //spIm.getViewport().setViewPosition(scroll);
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        SwingUtilities.updateComponentTreeUI(this.getParent());

        // We changed the position of the underlying picture, take it into account
        //lastX = e.getX() + (lastX - e.getX());	// lastX = lastX
        //lastY = e.getY() + (lastY - e.getY());	// lastY = lastY
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

    /**
     * Process image click and call parrent's methods
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && Objects.nonNull(imSize)) {
            changeViewImage();
        }
//        if ((e.getModifiers() == InputEvent.BUTTON2_MASK) || (e.getModifiers() == InputEvent.BUTTON3_MASK))
//            parentComponent.changeViewedImage();

//        if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
//            if (imSize == null) {
//                // Клик по пустому изображению
//                parentComponent.clickImage(e.getX(), e.getY());
//                return;
//            }
//
//            double k = (double) imSize.width / panelSize.width;
//            int x = (int) (k * e.getX());
//            int y = (int) (k * e.getY());
//            if ((x < imSize.width) && (y < imSize.height))
//                parentComponent.clickImage(x, y);
//        }
    }

    private void changeViewImage() {
        if (currentViewImage.equals(originImage)) {
            setImage(changedImage, true);
        } else if (currentViewImage.equals(changedImage)) {
            setImage(originImage, true);
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
        switch (filterMode) {
            case CHANGE_VIEW_MODE -> {

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
                int matrixSize = 11; // FIXME: it's parameter
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
                gammaCorrection(2); // FIXME: check limits
            }
            case ROBERTS_OPERATOR -> {
                int[][] horizontalMatrix = FilterMatrices.ROBERTS_HORIZONTAL_MATRIX;
                int[][] verticalMatrix = FilterMatrices.ROBERTS_VERTICAL_MATRIX;
                double threshold = 0.6;
                twoDimensionalEdgeDetectionFilter(horizontalMatrix, verticalMatrix, threshold);
            }
            case SOBEL_OPERATOR -> {
                int[][] horizontalMatrix = FilterMatrices.SOBEL_HORIZONTAL_MATRIX;
                int[][] verticalMatrix = FilterMatrices.SOBEL_VERTICAL_MATRIX;
                double threshold = 0.6; // FIXME: it's parameter
                twoDimensionalEdgeDetectionFilter(horizontalMatrix, verticalMatrix, threshold);
            }
            case FLOYD_STEINBERG_DITHERING -> {
                int redQuantization = 4; // FIXME: it's parameter
                int greenQuantization = 8;
                int blueQuantization = 32;
                floydSteinbergDithering(redQuantization, greenQuantization, blueQuantization);
            }
            case ORDERLY_DITHERING -> {
                int redQuantization = 4; // FIXME: it's parameter
                int greenQuantization = 8;
                int blueQuantization = 32;
                orderlyDithering(redQuantization, greenQuantization, blueQuantization);
            }
            case AQUA_REALIZATION -> {
                medianFilter(5);
                int[][] sharpenMatrix = FilterMatrices.SHARPEN_MATRIX;
                applyMatrixFilter(changedImage, sharpenMatrix);
            }
            case ROTATE -> {

            }
            case RETRO_EFFECT -> {
                int noiseLimit = 35; // FIXME
                addNoise(noiseLimit);
                int[][] matrixFilter = FilterMatrices.GAUSS_5X5_MATRIX;
                applyMatrixFilter(changedImage, matrixFilter);
                retroEffectFilter(changedImage);
            }
        }
        currentViewImage = changedImage;
        setImage(currentViewImage, true);
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

                int newRedComponent = Math.min(redComponent + 128, 255);
                int newGreenComponent = Math.min(greenComponent + 128, 255);
                int newBlueComponent = Math.min(blueComponent + 128, 255);
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

                redComponent += generator.nextInt(-noiseLimit, noiseLimit);
                greenComponent += generator.nextInt(-noiseLimit, noiseLimit);
                blueComponent += generator.nextInt(-noiseLimit, noiseLimit);

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
        return oldColor * quantization / 256.0; // TODO: check later (255 or 256)?
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
