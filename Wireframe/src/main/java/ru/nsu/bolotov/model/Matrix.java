package ru.nsu.bolotov.model;

import java.util.Arrays;

public class Matrix {
    private final int rows;
    private final int columns;
    private final double[][] elements;

    public Matrix(double[][] elements) {
        this.rows = elements.length;
        this.columns = elements[0].length;
        this.elements = elements;
    }

    public Matrix(Matrix other) {
        this.rows = other.rows;
        this.columns = other.columns;
        this.elements = Arrays.copyOf(other.elements, other.elements.length);
    }

    public double[][] getElements() {
        return elements;
    }

    public double getValueByPosition(int row, int column) {
        if (row < 0 || row >= this.rows || column < 0 || column >= this.columns) {
            throw new IllegalArgumentException("Illegal position");
        }
        return this.elements[row][column];
    }

    public static double[] multiplyMatrixByVector(Matrix matrix, double[] vector) {
        if (matrix.columns != vector.length) {
            throw new IllegalArgumentException("Cannot multiply matrix by vector");
        }
        double[] resultVector = new double[vector.length];
        for (int i = 0; i < matrix.rows; ++i) {
            for (int j = 0; j < matrix.columns; ++j) {
                resultVector[i] += matrix.getValueByPosition(i, j) * vector[j];
            }
        }
        return resultVector;
    }

    public static FourCoordinatesVector multiplyMatrixByVector(Matrix matrix, FourCoordinatesVector vector) {
        double[] pointCoordinates = new double[]{vector.getX(), vector.getY(), vector.getZ(), 1.0};
        double[] result = new double[pointCoordinates.length];
        for (int i = 0; i < matrix.rows; ++i) {
            for (int j = 0; j < matrix.columns; ++j) {
                result[i] += matrix.getValueByPosition(i, j) * pointCoordinates[j];
            }
        }
        return new FourCoordinatesVector(result[0] / result[3], result[1] / result[3], result[2] / result[3]);
    }

    public static Matrix multiplyMatrixByMatrix(Matrix first, Matrix second) {
        if (first.columns != first.rows) {
            throw new IllegalArgumentException("Cannot multiply matrix by matrix");
        }
        Matrix result = new Matrix(new double[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        for (int i = 0; i < first.rows; ++i) {
            for (int j = 0; j < first.columns; ++j) {
                for (int k = 0; k < result.rows; ++k) {
                    result.elements[i][j] += first.getValueByPosition(i, k) * second.getValueByPosition(k, j);
                }
            }
        }
        return result;
    }
}
