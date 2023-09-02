package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class TableCellEditor extends DefaultCellEditor {
    private final JTextField editorComponent;

    public TableCellEditor() {
        super(new JTextField());
        editorComponent = (JTextField) getComponent();

        editorComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateInput();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editorComponent.setText(value != null ? value.toString() : "");
        editorComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateInput();
            }
        });

        return editorComponent;
    }

    private void validateInput() {
        String inputValue = editorComponent.getText();

        try {
            Integer.parseInt(inputValue, 16);
            if (inputValue.length() != 2) throw new RuntimeException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Введите правильное шестнадцатеричное число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            cancelCellEditing();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Введите число с 2 цифрами", "Ошибка", JOptionPane.ERROR_MESSAGE);
            cancelCellEditing();
        }
    }
}
