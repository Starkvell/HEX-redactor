package org.example.model;

import javax.swing.*;

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
