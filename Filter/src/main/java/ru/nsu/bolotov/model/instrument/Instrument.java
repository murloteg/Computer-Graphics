package ru.nsu.bolotov.model.instrument;

import javax.swing.*;
import java.util.List;

public interface Instrument {
    JButton getInstrumentButton();
    JRadioButtonMenuItem getMenuButton();
    String getInstrumentName();
    void injectActionListeners(List<Instrument> instruments, ButtonGroup buttonGroup);
}
