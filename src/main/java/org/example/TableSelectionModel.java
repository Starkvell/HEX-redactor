package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TableSelectionModel extends DefaultListSelectionModel {
    public TableSelectionModel() {
        super();
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (index1 != 0) {
            super.setSelectionInterval(index0, index1);
        }
    }


}
