package org.example;

import org.example.controller.HexController;
import org.example.model.MyTableModel;
import org.example.model.TableSelectionModel;
import org.example.utility.FileMode;
import org.example.view.menu.MenuManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;

public class HexGUI extends JFrame {
    private MyTableModel model;
    private HexController controller;
    private StatusBar statusBar;
    private MenuManager menuManager;
    private JFileChooser fileChooser;

    private TableSelectionModel tableColumnSelectionModel;
    private ListSelectionListener listSelectionModelListener;

    private JPanel mainPanel;
    private JTable hexTable;
    private JScrollPane hexScrollPane;


    private int columnCount = 16;



    HexGUI() {
        menuManager = new MenuManager();
        fileChooser = new JFileChooser();
        tableColumnSelectionModel = new TableSelectionModel();
        model = new MyTableModel();
        controller = new HexController(model, this);

        createMenu(); // Создаем меню
        setUpTable(); // Настройка таблицы
        initFrameUI(); // Инициализируем фрейм

        addColumnSelectionListener(this.listSelectionModelListener);
        addRowSelectionListener(this.listSelectionModelListener);
    }

    public void setListSelectionModelListener(ListSelectionListener listSelectionModelListener) {
        this.listSelectionModelListener = listSelectionModelListener;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    private void setUpTable() {
        this.hexTable.setModel(model);
        this.hexTable.getTableHeader().setReorderingAllowed(false);
        this.hexTable.getTableHeader().setResizingAllowed(false);
        this.hexTable.setCellSelectionEnabled(true);
        this.hexTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.hexTable.getColumnModel().setSelectionModel(tableColumnSelectionModel);
        this.hexTable.setDefaultEditor(Object.class, new TableCellEditor());
    }

    public int getColumnCount() {
        return columnCount;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public MyTableModel getTableModel() {
        return model;
    }

    public JTable getHexTable() {
        return hexTable;
    }

    public StatusBar getStatusBarView() {
        return statusBar;
    }

    public File selectFile(FileMode fileMode) throws IOException {
        int returnValue;
        if (fileMode == FileMode.OPEN)
            returnValue = fileChooser.showOpenDialog(this);
        else
            returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else throw new IOException("Файл не выбран");
    }

    private void createMenu() {
        setJMenuBar(menuManager.getjMenuBar());
    }

    public void setStartCursor(){
        // Задаем начальный курсор на 1 ячейке
        this.hexTable.setColumnSelectionInterval(1, 1);
        this.hexTable.setRowSelectionInterval(0, 0);
    }

    public void deleteRowSelectionListener(ListSelectionListener listSelectionListener){
        // Удаление слушателя
        hexTable.getSelectionModel().removeListSelectionListener(listSelectionListener);
    }

    private void addRowSelectionListener(ListSelectionListener listSelectionListener) {
        hexTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    private void addColumnSelectionListener(ListSelectionListener listSelectionListener){
        hexTable.getColumnModel().getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    private void deleteColumnSelectionListener(ListSelectionListener listSelectionListener){
        hexTable.getColumnModel().getSelectionModel().removeListSelectionListener(listSelectionListener);
    }

    public void addScrollAdjustmentListener(AdjustmentListener adjustmentListener){
        hexScrollPane.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
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

    public String getSelectedData() {
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


    private void initFrameUI() {
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1300,600));
        this.setPreferredSize(new Dimension(1300,700));
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HexGUI::new);
    }

}
