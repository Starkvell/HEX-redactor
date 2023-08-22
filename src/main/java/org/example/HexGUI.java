package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HexGUI extends JFrame {
    private JPanel mainPanel;
    private JMenuBar jMenuBar;
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
    private String[] hexString;
    private int columnCount = 16;


    HexGUI() {
        createMenu(); // Создаем меню
        initFrameUI(); // Инициализируем фрейм
    }

    private void createMenu() {
        this.jMenuBar = new JMenuBar();

        createFileMenu(); // Создать меню File
        createEditMenu(); // Создать меню Edit
        createHelpMenu(); // Создать меню Help

        setJMenuBar(jMenuBar);
    }

    private void createHelpMenu() {
        JMenu jmHelp = new JMenu("Help");
        JMenuItem jmiAbout = new JMenuItem("About");
        jmHelp.add(jmiAbout);
        jMenuBar.add(jmHelp);
    }

    private void createEditMenu() {
        JMenu jmEdit = new JMenu("Edit");
        JMenuItem jmiCut = new JMenuItem("Cut");
        JMenuItem jmiCopy = new JMenuItem("Copy");
        JMenuItem jmiPaste = new JMenuItem("Paste");
        JMenuItem jmiDelete = new JMenuItem("Delete");
        JMenuItem jmiColumnCount = new JMenuItem("Change the number of columns");

        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);
        jmEdit.add(jmiDelete);
        jmEdit.add(jmiColumnCount);

        this.jMenuBar.add(jmEdit);

        jmiColumnCount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnCountInputDialog dialog = new ColumnCountInputDialog();
                dialog.setLocationRelativeTo(HexGUI.this);
                dialog.pack();
                dialog.setVisible(true);

                int columnCount = dialog.getColumnCount();
                if (columnCount > 0) {
                    HexGUI.this.columnCount = columnCount;
                    if (hexString != null) {
                        createTable(HexGUI.this.columnCount);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Введите положительное значение",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void createFileMenu() {
        JMenu jmFile = new JMenu("File");
        JMenuItem jmiOpen = new JMenuItem("Open");
        JMenuItem jmiClose = new JMenuItem("Close");
        jmiClose.setEnabled(false);
        JMenuItem jmiSave = new JMenuItem("Save");
        jmiSave.setEnabled(false);
        JMenuItem jmiExit = new JMenuItem("Exit");

        jmFile.add(jmiOpen);
        jmFile.add(jmiClose);
        jmFile.add(jmiSave);
        jmFile.addSeparator();
        jmFile.add(jmiExit);

        this.jMenuBar.add(jmFile);

        // Ввести приемники событий от пунктов меню
        jmiOpen.addActionListener(l -> {
            if (openFile()) {
                jmiClose.setEnabled(true);
                jmiSave.setEnabled(true);
            }
        });

        jmiClose.addActionListener(l -> {
            closeFile();
            jmiClose.setEnabled(false);
            jmiSave.setEnabled(false);
        });

        jmiExit.addActionListener(l -> System.exit(0));
    }

    private void closeFile() {
        this.hexString = null;
        DefaultTableModel model = (DefaultTableModel) hexTable.getModel();
        model.setColumnCount(0);
    }

    private boolean openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            this.hexString = parseFile(selectedFile);
            createTable(columnCount);
            return true;
        } else {
            return false;
        }
    }

    private void createTable(int col) {
        // Инициализация моделей
        DefaultTableModel tableModel = new DefaultTableModel() {
            // Запрет редактирования 1 колонки
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        TableSelectionModel tableSelectionModel = new TableSelectionModel();

        // Настройка таблицы
        this.hexTable.setModel(tableModel);
        this.hexTable.getTableHeader().setReorderingAllowed(false);
        this.hexTable.getTableHeader().setResizingAllowed(false);
        this.hexTable.setCellSelectionEnabled(true);
        this.hexTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.hexTable.getColumnModel().setSelectionModel(tableSelectionModel);

        // Заполнение таблицы
        fillTable(col, tableModel);
        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) { // TODO: Сделать перед clearDataLabel()
                    updateSelectedDataLabel();
                }
            }
        };
        tableSelectionModel.addListSelectionListener(listSelectionListener);
        hexTable.getSelectionModel().addListSelectionListener(listSelectionListener);

        // Задаем начальный курсор на 1 ячейке
        this.hexTable.setColumnSelectionInterval(1, 1);
        this.hexTable.setRowSelectionInterval(0, 0);
    }

    private void updateSelectedDataLabel() {
        String selectedData = getSelectedData();
        if (selectedData == null) return;
        byte[] bytes = getBytes(selectedData);

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

    private static byte[] getBytes(String selectedData) {
        byte[] bytes = new byte[selectedData.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int intValue = Integer.parseInt(selectedData.substring(index, index + 2), 16);
            bytes[i] = (byte) intValue;
        }
        return bytes;
    }

    private String getSelectedData() {
        int selectedRow = hexTable.getSelectedRow();
        if (selectedRow == -1) return null;
        int[] selectedColumns = hexTable.getSelectedColumns();
        StringBuilder stringData = new StringBuilder();

        if (selectedColumns.length == 1 || selectedColumns.length == 2 || selectedColumns.length == 4 || selectedColumns.length == 8) {
            for (int col : selectedColumns) {
                Object value = hexTable.getValueAt(selectedRow, col);
                stringData.append(value);
            }
        }

        return stringData.toString();
    }

    private void updateUnsignedIntegerValueLabel(String stringData) {
        BigInteger bigInteger = new BigInteger(stringData, 16);
        unsignedIntegerValueLabel.setText(bigInteger.toString());
    }


    private void fillTable(int col, DefaultTableModel tableModel) {
        tableModel.addColumn("Offset");
        for (int i = 0; i < col; i++) {
            tableModel.addColumn(Integer.toHexString(i).toUpperCase()); // Установить заголовки
        }

        int row = (hexString.length / col) + 1;
        int k = 0;
        for (int i = 0; i < row; i++) {
            Object[] rowData = new Object[col + 1];
            rowData[0] = String.format("%04X", i * col); // Шестнадцатиричный сдвиг
            for (int j = 0; j < col && k < hexString.length; j++) {
                rowData[j + 1] = hexString[k++];
            }
            tableModel.addRow(rowData);
        }
    }

    private String[] parseFile(File file) {
        String[] result;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[(int) raf.length()];
            raf.readFully(buffer);

            StringBuilder hexContent = new StringBuilder();
            for (byte b : buffer) {
                hexContent.append(String.format("%02X ", b));
            }

            result = hexContent.toString().trim().split(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void initFrameUI() {
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HexGUI hexGUI = new HexGUI();
        });
    }
}
