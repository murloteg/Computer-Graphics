package ru.nsu.bolotov.model.uicomponent.instrument;

import javax.swing.*;
import java.util.List;

public interface Instrument {
    JButton getInstrumentButton();
    JRadioButtonMenuItem getMenuButton();
    String getInstrumentName();
    void injectActionListeners(List<Instrument> instruments, ButtonGroup buttonGroup);
    void changeSelectState();
    default void unselectOtherInstrumentsButtons(List<Instrument> instruments, Instrument caller) {
        for (Instrument instrument : instruments) {
            if (!instrument.equals(caller)) {
                instrument.changeSelectState();
            }
        }
    }
}
