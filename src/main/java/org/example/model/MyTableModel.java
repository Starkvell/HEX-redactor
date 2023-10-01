package org.example.model;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MyTableModel extends DefaultTableModel {

    /**
     * Constructs a default <code>DefaultTableModel</code>
     * which is a table of zero columns and zero rows.
     */
    public MyTableModel() {

    }

    private String[] hexString;
    private final int CountOfLoadingBytes = 500;

    // Устанавливает hexString в модель
    public void readFile(File filePath) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            byte[] buffer = new byte[CountOfLoadingBytes];
            int bytesRead = raf.read(buffer);
            if (bytesRead != -1){
                raf.seek(bytesRead);
            }

            StringBuilder hexContent = new StringBuilder();
            for (int i=0; i<bytesRead;i++) {
                hexContent.append(String.format("%02X ", buffer[i]));
            }

            hexString = hexContent.toString().trim().split(" ");
        }
    }

    public void clearData(){
        hexString = null;
    }
    public String[] getHexString() {
        return hexString;
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

        addColumn("Offset");   //TODO:Эту часть в конструктор clearTable отдельно
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
