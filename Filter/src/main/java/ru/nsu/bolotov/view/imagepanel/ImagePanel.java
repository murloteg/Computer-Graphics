package ru.nsu.bolotov.view.imagepanel;

import ru.nsu.bolotov.exception.FailedLoadImage;
import ru.nsu.bolotov.exception.FailedSaveImage;
import ru.nsu.bolotov.model.mode.FilterMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
     * @param newIm - image to view
     * @throws Exception - given JScrollPane must nor be null
     */
    public ImagePanel(JScrollPane scrollPane, JFrame parentComponent, BufferedImage newIm) throws Exception {
        this(scrollPane, parentComponent);
        setImage(newIm, true);
    }

//    @Override
//    public void paint(Graphics g) {
//        if (currentViewImage == null) {
//            g.setColor(Color.WHITE);
//            g.fillRect(0, 0, getWidth(), getHeight());
//        } else
//            g.drawImage(currentViewImage, 0, 0, panelSize.width, panelSize.height, null);
//    }

    @Override
    public void paintComponent(Graphics g) {
        if (currentViewImage == null) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else
            g.drawImage(currentViewImage, 0, 0, panelSize.width, panelSize.height, null);
    }

//	public void update(Graphics g)
//	{
//		paint(g);
//	}

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
                double[][] matrix = {
                        {0, 1, 0},
                        {-1, 0, 1},
                        {0, -1, 0}
                };
                applyMatrixFilter(matrix);
                for (int x = 0; x < originImage.getWidth(); ++x) {
                    for (int y = 0; y < originImage.getHeight(); ++y) {
                        int pixelColor = changedImage.getRGB(x, y);
                        int redComponent = pixelColor & 0x000000FF;
                        int greenComponent = (pixelColor >> 8) & 0x000000FF;
                        int blueComponent = (pixelColor >> 16) & 0x000000FF;

                        int newRedComponent = Math.min((redComponent + 128), 255);
                        int newGreenComponent = Math.min((greenComponent + 128), 255);
                        int newBlueComponent = Math.min((blueComponent + 128), 255);
                        int updatedColor = (newRedComponent + 128) | ((newGreenComponent + 128) << 8) | ((newBlueComponent + 128) << 16);
                        changedImage.setRGB(x, y, updatedColor);
                    }
                }
            }
            case SHARPNESS_INCREASING -> {
                double[][] matrix = {
                        {0, -1, 0},
                        {-1, 5, -1},
                        {0, -1, 0}
                };
                applyMatrixFilter(matrix);
            }
            case GAUSS_SMOOTHING -> {
                double[][] matrix = {
                        {1, 1, 1},
                        {1, 1, 1},
                        {1, 1, 1}
                };
                applyMatrixFilter(matrix);
            }
            case GAMMA_CORRECTION -> {

            }
            case ROBERTS_OPERATOR -> {
                double[][] matrix = {
                        {1, 1, 1},
                        {1, -5, 1},
                        {1, 1, 1}
                };
                applyMatrixFilter(matrix);
            }
            case SOBEL_OPERATOR -> {

            }
            case FLOYD_STEINBERG_DITHERING -> {

            }
            case ORDERLY_DITHERING -> {

            }
        }
        currentViewImage = changedImage;
        setImage(currentViewImage, true);
    }

    private int getMiddleColorForPixel(int pixelColor) {
        int redComponent = pixelColor & 0x000000FF;
        int greenComponent = (pixelColor >> 8) & 0x000000FF;
        int blueComponent = (pixelColor >> 16) & 0x000000FF;

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
                int redComponent = pixelColor & 0x000000FF;
                int greenComponent = (pixelColor >> 8) & 0x000000FF;
                int blueComponent = (pixelColor >> 16) & 0x000000FF;

                int updatedColor = (255 - redComponent) | ((255 - greenComponent) << 8) | ((255 - blueComponent) << 16);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

//    private int calculateXShiftFromCentralPosition(int currentPosition, int centralPosition, int matrixSize) {
//
//    }

    // Gaussian blur 3x3
    private void gaussSmoothing3x3(int matrixSize) {
        double[] smoothingMatrix = new double[matrixSize * matrixSize];
        int centralPosition = (matrixSize * matrixSize - 1) / 2;


    }

    private boolean isBorderedValue(int position, int width, int height) {
        return position < width || position >= (width * (height - 1)) || position % width == 0
                || position % width == (width - 1);
    }

    private double calcDivisor(double[][] filterMatrix) {
        double divisor = 0;
        int matrixRows = filterMatrix.length;
        for (int i = 0; i < matrixRows; ++i) {
            for (int j = 0; j < matrixRows; ++j) {
                divisor += filterMatrix[i][j];
            }
        }
        return Math.max(divisor, 1);
    }

    private int calculateMultiplicationResultForFilterMatrix(double[][] filterMatrix, int x, int y) {
        double result = 0;
        int matrixRows = filterMatrix.length;
        int matrixRadius = matrixRows / 2;
        for (int i = 0; i < matrixRows; ++i) {
            for (int j = 0; j < matrixRows; ++j) {
                int currentY = y + i - matrixRadius;
                int currentX = x + j - matrixRadius;
//                int currentPosition = currentY * originImage.getWidth() + currentX;
//                if (currentPosition < 0 || currentPosition >= originImage.getWidth()) {
//                    continue;
//                }
                int pixelColor = originImage.getRGB(currentX, currentY);
//                int redComponent = pixelColor & 0x000000FF;
//                int greenComponent = (pixelColor >> 8) & 0x000000FF;
//                int blueComponent = (pixelColor >> 16) & 0x000000FF;
//                result += filterMatrix[i][j] * (redComponent | (greenComponent << 8) | (blueComponent << 16));

                result += filterMatrix[i][j] * pixelColor / calcDivisor(filterMatrix);
            }
        }
        return (int) result;
    }

    private void applyMatrixFilter(double[][] filterMatrix) {
        int imageWidth = originImage.getWidth();
        int imageHeight = originImage.getHeight();
        for (int x = 0; x < imageWidth; ++x) {
            for (int y = 0; y < imageHeight; ++y) {
                int position = y * imageWidth + x;
                if (isBorderedValue(position, imageWidth, imageHeight)) {
                    continue;
                }
                int updatedColor = calculateMultiplicationResultForFilterMatrix(filterMatrix, x, y);
                changedImage.setRGB(x, y, updatedColor);
            }
        }
    }

    // Sharpness increase - sum of matrix values is 1

    // Embossing - sum of matrix values is 0
}
