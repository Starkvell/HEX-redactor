package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        this.hexString = parseFile(new File(System.getProperty("user.home") + "\\Desktop\\hello.txt")); // Читаем из файла и парсим побайтово через пробел в 16СС
        createTable(16); // Создаем таблицу
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
        JMenuItem jmiSave = new JMenuItem("Save");
        JMenuItem jmiExit = new JMenuItem("Exit");

        jmFile.add(jmiOpen);
        jmFile.add(jmiClose);
        jmFile.add(jmiSave);
        jmFile.addSeparator();
        jmFile.add(jmiExit);

        this.jMenuBar.add(jmFile);

        // Ввести приемники событий от пунктов меню
        jmiOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent l) {
                openFile();
            }
        });
    }

    private void createTable(int col) {
        DefaultTableModel tableModel = new DefaultTableModel(); // Установить модель по умолчанию
        this.hexTable.setModel(tableModel);

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
