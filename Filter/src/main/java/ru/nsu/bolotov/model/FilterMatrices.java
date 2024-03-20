package ru.nsu.bolotov.model;

public final class FilterMatrices {
    public static final int[][] EMBOSSING_MATRIX = {
            {0, 1, 0},
            {-1, 0, 1},
            {0, -1, 0}
    };
    public static final int[][] SHARPEN_MATRIX = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };
    public static final int[][] GAUSS_3X3_MATRIX = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
    };
    public static final int[][] GAUSS_5X5_MATRIX = {
            {1, 4, 6, 4, 1},
            {4, 16, 24, 16, 4},
            {6, 24, 36, 24, 6},
            {4, 16, 24, 16, 4},
            {1, 4, 6, 4, 1}
    };
    public static final int[][] ROBERTS_HORIZONTAL_MATRIX = {
            {0, 1},
            {-1, 0}
    };
    public static final int[][] ROBERTS_VERTICAL_MATRIX = {
            {1, 0},
            {0, -1}
    };
    public static final int[][] SOBEL_HORIZONTAL_MATRIX = {
            {1, 0, -1},
            {2, 0, -2},
            {1, 0, -1}
    };
    public static final int[][] SOBEL_VERTICAL_MATRIX = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
    };
    public static final int[][] ORDERLY_DITHERING_BASE_MATRIX = {
            {0, 2},
            {3, 1}
    };
    public static final int[][] CUSTOM_MATRIX = {
            {0, 0, -2, 0, 0},
            {0, -2, -1, 0, 0},
            {-1, -1, 1, 1, 1},
            {0, 0, 1, 2, 0},
            {0, 0, 2, 0, 0}
    };

    private FilterMatrices() {
        throw new IllegalStateException("Instantiation of util class");
    }
}
