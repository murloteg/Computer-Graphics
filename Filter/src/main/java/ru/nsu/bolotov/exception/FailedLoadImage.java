package ru.nsu.bolotov.exception;

public class FailedLoadImage extends RuntimeException {
    public FailedLoadImage(Exception exception) {
        super(exception);
    }
}
