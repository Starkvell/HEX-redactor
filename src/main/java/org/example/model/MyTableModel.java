package org.example.model;

import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel {

    /**
     * Constructs a default <code>DefaultTableModel</code>
     * which is a table of zero columns and zero rows.
     */
    public MyTableModel() {

    }

    // Запрет редактирования 1 колонки
    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    public void clearTable() {
        setColumnCount(0);
        setRowCount(0);
    }

    public void fillTable(int col, String[] hexString) {
        clearTable();

        addColumn("Offset");
        for (int i = 0; i < col; i++) {
            addColumn(Integer.toHexString(i).toUpperCase()); // Установить заголовки
        }


        int row = (hexString.length / col) + 1;
        int k = 0;
        for (int i = 0; i < row; i++) {
            Object[] rowData = new Object[col + 1];
            rowData[0] = String.format("%04X", i * col); // Шестнадцатиричный сдвиг
            for (int j = 0; j < col && k < hexString.length; j++) {
                rowData[j + 1] = hexString[k++];
            }
            addRow(rowData);
        }
    }
}
