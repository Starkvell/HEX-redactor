package org.example.view;

import javax.swing.*;


public class MenuManager {
    private JMenuBar jMenuBar;
    private FileMenuManager fileMenuManager;
    private EditMenuManager editMenuManager;
    private HelpMenuManager helpMenuManager;

    public MenuManager() {
        this.jMenuBar = new JMenuBar();
        this.fileMenuManager = new FileMenuManager();
        this.editMenuManager = new EditMenuManager();
        this.helpMenuManager = new HelpMenuManager();

        createFileMenu(); // Создать меню File
        createEditMenu(); // Создать меню Edit
        createHelpMenu(); // Создать меню Help
    }


    private void createFileMenu() {
        this.jMenuBar.add(fileMenuManager.getJMenuFile());
    }

    private void createEditMenu() {
        this.jMenuBar.add(editMenuManager.getJMenuEdit());
    }

    private void createHelpMenu() {
        this.jMenuBar.add(helpMenuManager.getJMenuHelp());
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

    public HelpMenuManager getHelpMenuManager() {
        return helpMenuManager;
    }
}
