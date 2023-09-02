package org.example.view.menu;

import javax.swing.*;


public class MenuManager {
    private JMenuBar jMenuBar;
    private FileMenuManager fileMenuManager;
    private EditMenuManager editMenuManager;

    public MenuManager() {
        this.jMenuBar = new JMenuBar();
        this.fileMenuManager = new FileMenuManager();
        this.editMenuManager = new EditMenuManager();

        createFileMenu();
        createEditMenu();
    }


    private void createFileMenu() {
        this.jMenuBar.add(fileMenuManager.getJMenuFile());
    }

    private void createEditMenu() {
        this.jMenuBar.add(editMenuManager.getJMenuEdit());
    }

    public JMenuBar getjMenuBar() {
        return jMenuBar;
    }

    public EditMenuManager getEditMenuManager() {
        return editMenuManager;
    }

    public FileMenuManager getFileMenuManager() {
        return fileMenuManager;
    }
}
