package org.example.view;

import javax.swing.*;
import java.awt.event.*;

public class SearchDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonDown;
    private JButton buttonUp;
    private JTextField searchField;

    public SearchDialog() {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDown);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }


    public void addSearchListener(ActionListener listener) {
        buttonDown.addActionListener(listener);
    }

    public String getSearchText() {
        return searchField.getText();
    }

}
