package org.example.view.menu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class EditMenuManager {
    private JMenu jmEdit;

    private JMenuItem jmiCut;
    private JMenuItem jmiCopy;
    private JMenuItem jmiPaste;
    private JMenuItem jmiDelete;
    private JMenuItem jmiColumnCount;

    public EditMenuManager() {
        this.jmEdit = new JMenu("Edit");
        this.jmiCut = new JMenuItem("Cut");
        this.jmiCopy = new JMenuItem("Copy");
        this.jmiPaste = new JMenuItem("Paste");
        this.jmiDelete = new JMenuItem("Delete");
        this.jmiColumnCount = new JMenuItem("Change the number of columns");

        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);
        jmEdit.add(jmiDelete);
        jmEdit.add(jmiColumnCount);
    }

    public void addChangeColumnCountListener(ActionListener listener) {
        jmiColumnCount.addActionListener(listener);
    }

    public JMenu getJMenuEdit() {
        return jmEdit;
    }
}
