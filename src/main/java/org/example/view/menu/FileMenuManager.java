package org.example.view.menu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FileMenuManager {
    private JMenu jmFile;

    private JMenuItem jmiOpen;
    private JMenuItem jmiClose;
    private JMenuItem jmiSaveAs;
    private JMenuItem jmiExit;

    public FileMenuManager() {
        this.jmFile = new JMenu("File");

        this.jmiOpen = new JMenuItem("Open");
        this.jmiClose = new JMenuItem("Close");
        jmiClose.setEnabled(false);
        this.jmiSaveAs = new JMenuItem("Save as");
        jmiSaveAs.setEnabled(false);
        this.jmiExit = new JMenuItem("Exit");

        jmFile.add(jmiOpen);
        jmFile.add(jmiClose);
        jmFile.add(jmiSaveAs);
        jmFile.addSeparator();
        jmFile.add(jmiExit);
    }

    public void addOpenFileListener(ActionListener listener) {
        this.jmiOpen.addActionListener(listener);
    }

    public void addCloseFileListener(ActionListener listener) {
        this.jmiClose.addActionListener(listener);
    }

    public void addSaveFileAsListener(ActionListener listenern) {
        this.jmiSaveAs.addActionListener(listenern);
    }

    public void addExitListener(ActionListener listener) {
        this.jmiExit.addActionListener(listener);
    }

    public void enableSaveAsButton(boolean b) {
        jmiSaveAs.setEnabled(b);
    }

    public void enableOpenFileButton(boolean b) {
        jmiOpen.setEnabled(b);
    }

    public void enableCloseFileButton(boolean b) {
        jmiClose.setEnabled(b);
    }

    public JMenu getJMenuFile() {
        return jmFile;
    }
}
