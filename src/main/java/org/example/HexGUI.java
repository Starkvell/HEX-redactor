package org.example;

import org.example.controller.HexController;
import org.example.model.HexModel;
import org.example.view.MenuManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HexGUI extends JFrame {
    private HexModel model;
    private HexController controller;

    private MenuManager menuManager;
    private JFileChooser fileChooser;

    private TableSelectionModel tableColumnSelectionModel;
    private TableModel tableModel;
    private ListSelectionListener listSelectionModelListener;

    private JPanel mainPanel;
    private JTable hexTable;
    private JScrollPane hexScrollPane;
    private JLabel integerLabel;
    private JLabel integerValueLabel;
    private JLabel unsignedIntegerLabel;
    private JLabel unsignedIntegerValueLabel;
    private JLabel floatLabel;
    private JLabel floatValueLabel;
    private JLabel doubleLabel;
    private JLabel doubleValueLabel;

    private int columnCount = 16;


    //TODO: Создать отдельный класс по таблицу TableManager
    HexGUI() {
        menuManager = new MenuManager();
        fileChooser = new JFileChooser();
        tableModel = new MyTableModel();
        tableColumnSelectionModel = new TableSelectionModel();
        model = new HexModel();
        controller = new HexController(model, this);

        createMenu(); // Создаем меню
        setUpTable(); // Настройка таблицы
        initFrameUI(); // Инициализируем фрейм

        addTableSelectionModelListener(this.listSelectionModelListener);
    }

    public void setListSelectionModelListener(ListSelectionListener listSelectionModelListener) {
        this.listSelectionModelListener = listSelectionModelListener;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    private void setUpTable() {
        this.hexTable.setModel(tableModel);
        this.hexTable.getTableHeader().setReorderingAllowed(false);
        this.hexTable.getTableHeader().setResizingAllowed(false);
        this.hexTable.setCellSelectionEnabled(true);
        this.hexTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.hexTable.getColumnModel().setSelectionModel(tableColumnSelectionModel);
    }

    public int getColumnCount() {
        return columnCount;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public JTable getHexTable() {
        return hexTable;
    }

    public File selectFile() throws IOException {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else throw new IOException("Файл не выбран");
    }

    private void createMenu() {
        setJMenuBar(menuManager.getjMenuBar());
    }

    public void createTable(int col) {

        // Заполнение таблицы
        fillTable(col, (DefaultTableModel) tableModel);

        // Задаем начальный курсор на 1 ячейке
        this.hexTable.setColumnSelectionInterval(1, 1);
        this.hexTable.setRowSelectionInterval(0, 0);
    }

    public void clearTable(){
        // Удаление слушателя перед очисткой таблицы
        ListSelectionListener listSelectionListener = this.listSelectionModelListener;
        hexTable.getSelectionModel().removeListSelectionListener(listSelectionListener);

        // Очистка таблицы
        DefaultTableModel model = (DefaultTableModel) hexTable.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);

        hexTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void addTableSelectionModelListener(ListSelectionListener listener) {
        hexTable.getSelectionModel().addListSelectionListener(listener);
        tableColumnSelectionModel.addListSelectionListener(listener);
    }

    public void clearDataLabel() {
        integerValueLabel.setText("");
        unsignedIntegerValueLabel.setText("");
        floatValueLabel.setText("");
        doubleValueLabel.setText("");
    }


    public void updateSelectedDataLabel() {
        try {
            String selectedData = getSelectedData();
            byte[] bytes = getBytesFromHex(selectedData);

            if (selectedData.length() == 2) {
                updateIntegerValueLabelForByte(bytes);
                updateUnsignedIntegerValueLabel(selectedData);
            } else if (selectedData.length() == 4) {
                updateIntegerValueLabelForShort(bytes);
                updateUnsignedIntegerValueLabel(selectedData);
            } else if (selectedData.length() == 8) {
                updateIntegerValueLabelForInt(bytes);
                updateUnsignedIntegerValueLabel(selectedData);
            } else if (selectedData.length() == 16) {
                updateIntegerValueLabelForLong(bytes);
                updateUnsignedIntegerValueLabel(selectedData);
                updateFloatValueLabel(bytes);
                updateDoubleValueLabel(bytes);
            }
        } catch (RuntimeException runtimeException){
            clearDataLabel();
        }
    }

    private void updateDoubleValueLabel(byte[] bytes) {
        double doubleValue = ByteBuffer.wrap(bytes).getDouble();
        doubleValueLabel.setText(String.valueOf(doubleValue));
    }

    private void updateFloatValueLabel(byte[] bytes) {
        float floatValue = ByteBuffer.wrap(bytes).getFloat();
        floatValueLabel.setText(String.valueOf(floatValue));
    }

    private void updateIntegerValueLabelForLong(byte[] bytes) {
        long integerValue = ByteBuffer.wrap(bytes).getLong();
        integerValueLabel.setText(String.valueOf(integerValue));
    }

    private void updateIntegerValueLabelForInt(byte[] bytes) {
        int integerValue = ByteBuffer.wrap(bytes).getInt();
        integerValueLabel.setText(String.valueOf(integerValue));
    }

    private void updateIntegerValueLabelForShort(byte[] bytes) {
        short shortValue = ByteBuffer.wrap(bytes).getShort();
        integerValueLabel.setText(String.valueOf(shortValue));
    }

    private void updateIntegerValueLabelForByte(byte[] bytes) {
        byte byteValue = ByteBuffer.wrap(bytes).get();
        integerValueLabel.setText(String.valueOf(byteValue));
    }

    private void updateUnsignedIntegerValueLabel(String stringData) {
        BigInteger bigInteger = new BigInteger(stringData, 16);
        unsignedIntegerValueLabel.setText(bigInteger.toString());
    }

    public static byte[] getBytesFromHex(String hexData) {
        byte[] bytes = new byte[hexData.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int intValue = Integer.parseInt(hexData.substring(index, index + 2), 16);
            bytes[i] = (byte) intValue;
        }
        return bytes;
    }

    private String getSelectedData() {
        int selectedRow = hexTable.getSelectedRow();
        if (selectedRow == -1 ) throw new RuntimeException("No data selected");
        int[] selectedColumns = hexTable.getSelectedColumns();
        StringBuilder stringData = new StringBuilder();

        if (selectedColumns.length == 1 || selectedColumns.length == 2 || selectedColumns.length == 4 || selectedColumns.length == 8) {
            for (int col : selectedColumns) {
                Object value = hexTable.getValueAt(selectedRow, col);
                if (value == null) {
                    throw new NullPointerException("The data must not contain null");
                }
                stringData.append(value);
            }
        }

        return stringData.toString();
    }


    private void fillTable(int col, DefaultTableModel tableModel) {
        clearTable();

        tableModel.addColumn("Offset");
        for (int i = 0; i < col; i++) {
            tableModel.addColumn(Integer.toHexString(i).toUpperCase()); // Установить заголовки
        }

        int row = (model.getHexString().length / col) + 1;
        int k = 0;
        for (int i = 0; i < row; i++) {
            Object[] rowData = new Object[col + 1];
            rowData[0] = String.format("%04X", i * col); // Шестнадцатиричный сдвиг
            for (int j = 0; j < col && k < model.getHexString().length; j++) {
                rowData[j + 1] = model.getHexString()[k++];
            }
            tableModel.addRow(rowData);
        }
    }

    private void initFrameUI() {
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HexGUI hexGUI = new HexGUI();
        });
    }

}

class MyTableModel extends DefaultTableModel {
    // Запрет редактирования 1 колонки
    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }
}
