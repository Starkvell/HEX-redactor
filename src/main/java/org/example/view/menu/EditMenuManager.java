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
    private JMenuItem jmiFind;

    public EditMenuManager() {
        this.jmEdit = new JMenu("Edit");
        this.jmiCut = new JMenuItem("Cut");
        this.jmiCopy = new JMenuItem("Copy");
        this.jmiPaste = new JMenuItem("Paste");
        this.jmiDelete = new JMenuItem("Delete");
        this.jmiColumnCount = new JMenuItem("Change the number of columns");
        this.jmiFind = new JMenuItem("Find");

        jmiCut.setEnabled(false);
        jmiCopy.setEnabled(false);
        jmiPaste.setEnabled(false);
        jmiDelete.setEnabled(false);
        jmiFind.setEnabled(false);

        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);
        jmEdit.add(jmiDelete);
        jmEdit.add(jmiColumnCount);
        jmEdit.add(jmiFind);
    }

    public void addChangeColumnCountListener(ActionListener listener) {
        jmiColumnCount.addActionListener(listener);
    }

    public void addFindListener(ActionListener listener){
        jmiFind.addActionListener(listener);
    }

    public void addDeleteListener(ActionListener listener){
        jmiDelete.addActionListener(listener);
    }

    public void enableFindButton(boolean b){
        jmiFind.setEnabled(b);
    }

    public void enableCutButton(boolean b){
        jmiCut.setEnabled(b);
    }

    public void enableCopyButton(boolean b){
        jmiCopy.setEnabled(b);
    }

    public void enablePasteButton(boolean b){
        jmiPaste.setEnabled(b);
    }

    public void enableDeleteButton(boolean b){
        jmiDelete.setEnabled(b);
    }

    public JMenu getJMenuEdit() {
        return jmEdit;
    }
}
