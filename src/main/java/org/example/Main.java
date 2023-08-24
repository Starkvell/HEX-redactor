package org.example;

import org.example.controller.HexController;
import org.example.model.HexModel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HexGUI();
            }
        });
    }
}