package ru.nsu.bolotov.exception;

public class FailedSaveImage extends RuntimeException {
    public FailedSaveImage(Exception exception) {
        super(exception);
    }
}
