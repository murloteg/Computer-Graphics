package ru.nsu.bolotov.model.uicomponent.instrument;

import java.util.List;

public interface DialogEnabled {
    List<?> getDefaultParameters();
    void showDialog();
}
