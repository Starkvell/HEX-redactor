package org.example.model;

import javax.swing.table.DefaultTableModel;
import java.io.*;

public class MyTableModel extends DefaultTableModel {
    private final int CountOfLoadingBytes = 1024;
    private long bytePosition = 0;

    public String[] readFileAndLoadNewPieceOfData(File filePath) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            byte[] buffer = new byte[CountOfLoadingBytes];
            raf.seek(bytePosition);
            int bytesRead = raf.read(buffer);
            if (bytesRead != -1) {
                bytePosition += bytesRead;
                StringBuilder hexContent = new StringBuilder();
                for (int i = 0; i < bytesRead; i++) {
                    hexContent.append(String.format("%02X ", buffer[i]));
                }

                return hexContent.toString().trim().split(" ");
            } else {
                return null;
            }
        }
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
        if (hexString == null) return;
        int rowInTable = this.getRowCount();
        int row = (hexString.length % col == 0) ? (hexString.length / col) : (hexString.length / col + 1);
        int k = 0;

        for (int i = 0; i < row; i++) {
            Object[] rowData = new Object[col + 1];
            rowData[0] = String.format("%04X", rowInTable * col + i * col); // Шестнадцатиричный сдвиг
            for (int j = 0; j < col && k < hexString.length; j++) {
                rowData[j + 1] = hexString[k++];
            }
            addRow(rowData);
        }
    }

    public void createTable(int col) {
        bytePosition = 0;
        addColumn("Offset");
        for (int i = 0; i < col; i++) {
            addColumn(Integer.toHexString(i).toUpperCase()); // Установить заголовки
        }
    }

}
