package org.example.view;

import javax.swing.*;

public class HelpMenuManager {
    private JMenu jmHelp;
    private JMenuItem jmiAbout;

    public HelpMenuManager() {
        this.jmHelp = new JMenu("Help");
        this.jmiAbout = new JMenuItem("About");

        jmHelp.add(jmiAbout);
    }

    public JMenu getJMenuHelp() {
        return jmHelp;
    }
}
