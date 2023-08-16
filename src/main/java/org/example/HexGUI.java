package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HexGUI extends JFrame {
    private JPanel mainPanel;
    private JMenuBar jMenuBar;
    private JTextArea resultTextArea;
    private JTable hexTable;
    private JScrollPane hexScrollPane;
    private JScrollPane resultScrollPane;
    private String[] hexString;


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

        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);
        jmEdit.add(jmiDelete);

        this.jMenuBar.add(jmEdit);
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
            createTable(16); //TODO:Добавить всплывающий список с выбором кол-ва столбцов
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

        // Задаем начальный курсор на 1 ячейке
        this.hexTable.setColumnSelectionInterval(1, 1);
        this.hexTable.setRowSelectionInterval(0, 0);

        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent l) {
                if (!l.getValueIsAdjusting()) {
                    int selectedRow = hexTable.getSelectedRow();
                    int selectedColumn = hexTable.getSelectedColumn();

                    if (selectedRow >= 0 && selectedColumn > 0) {
                        int endRow = Math.min(selectedRow + 3, hexTable.getRowCount() - 1);
                        int endColumn = Math.min(selectedColumn + 3, hexTable.getColumnCount() - 1);

                        hexTable.setColumnSelectionInterval(selectedColumn, endColumn);
                        hexTable.setRowSelectionInterval(selectedRow, endRow);   //TODO ДОДЕЛАТЬ БАГ
                    }
                }
            }
        };

        this.hexTable.getSelectionModel().addListSelectionListener(selectionListener);
        this.hexTable.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);


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
