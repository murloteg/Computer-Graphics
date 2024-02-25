package ru.nsu.bolotov.model.instrument;

import javax.swing.*;
import java.util.List;

public interface PaintInstrument {
    void injectActionListeners(List<PaintInstrument> instruments, ButtonGroup buttonGroup);
    void setButtonIcon(Icon buttonIcon);
    String getInstrumentName();
    JButton getToolBarButton();
    JRadioButtonMenuItem getMenuBarButton();
    void changeSelectState();
    default void unselectOtherInstrumentsButtons(List<PaintInstrument> instruments, PaintInstrument caller) {
        for (PaintInstrument instrument : instruments) {
            if (!instrument.equals(caller)) {
                instrument.changeSelectState();
            }
        }
    }
}
