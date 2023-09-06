package org.example.controller;

import org.example.view.ColumnCountInputDialog;
import org.example.HexGUI;
import org.example.utility.Pair;
import org.example.model.HexModel;
import org.example.view.SearchDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.example.HexGUI.getBytesFromHex;

public class HexController {
    private HexModel model;
    private HexGUI view;
    private SearchDialog searchDialog;

    public HexController(HexModel model, HexGUI view) {
        this.model = model;
        this.view = view;
        this.searchDialog = createSearchDialog(view);

        view.getMenuManager().getFileMenuManager().addOpenFileListener(new OpenFileListener());
        view.getMenuManager().getFileMenuManager().addCloseFileListener(new CloseFileListener());
        view.getMenuManager().getFileMenuManager().addSaveFileAsListener(new SaveFileAsListener());
        view.getMenuManager().getFileMenuManager().addExitListener(new ExitListener());
        view.getMenuManager().getEditMenuManager().addChangeColumnCountListener(new ChangeColumnCountListener());
        view.getMenuManager().getEditMenuManager().addFindListener(new FindListener());
        view.getMenuManager().getEditMenuManager().addDeleteListener(new DeleteListener());
        view.getMenuManager().getEditMenuManager().addCopyListener(new CopyListener());
        view.getMenuManager().getEditMenuManager().addCutListener(new CutListener());
        view.getMenuManager().getEditMenuManager().addPasteListener(new PasteListener());
        view.setListSelectionModelListener(new TableSelectionModelListener());
    }

    private SearchDialog createSearchDialog(HexGUI view) {
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.setLocationRelativeTo(view);
        searchDialog.pack();
        searchDialog.addSearchListener(new SearchListener());
        return searchDialog;
    }

    class OpenFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.getStatusBarView().clearDataLabel();
            if (openFile()) {
                view.getMenuManager().getFileMenuManager().enableSaveAsButton(true);
                view.getMenuManager().getFileMenuManager().enableCloseFileButton(true);
                view.getMenuManager().getFileMenuManager().enableOpenFileButton(false);
                view.getMenuManager().getEditMenuManager().enableFindButton(true);
                view.getMenuManager().getEditMenuManager().enableDeleteButton(true);
                view.getMenuManager().getEditMenuManager().enableCopyButton(true);
                view.getMenuManager().getEditMenuManager().enableCutButton(true);
                view.getMenuManager().getEditMenuManager().enablePasteButton(true);
            }
        }
    }

    private class CloseFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeFile();
            view.getMenuManager().getFileMenuManager().enableCloseFileButton(false);
            view.getMenuManager().getFileMenuManager().enableSaveAsButton(false);
            view.getMenuManager().getFileMenuManager().enableOpenFileButton(true);
            view.getMenuManager().getEditMenuManager().enableFindButton(false);
            view.getMenuManager().getEditMenuManager().enableDeleteButton(false);
            view.getMenuManager().getEditMenuManager().enableCopyButton(false);
            view.getMenuManager().getEditMenuManager().enableCutButton(false);
            view.getMenuManager().getEditMenuManager().enablePasteButton(false);
        }
    }

    private class SaveFileAsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                saveFileAs();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,
                        "Упс, файл не был сохранен",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ChangeColumnCountListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ColumnCountInputDialog dialog = new ColumnCountInputDialog();
            dialog.setLocationRelativeTo(view);
            dialog.pack();
            dialog.setVisible(true);

            int columnCount = dialog.getColumnCount();
            if (columnCount > 0) {
                view.setColumnCount(columnCount);
                if (model.getHexString() != null) {
                    view.getTableModel().fillTable(view.getColumnCount(), model.getHexString());
                    view.setStartCursor();
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Введите положительное значение",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class TableSelectionModelListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                updateSelectedDataLabel();
            }
        }
    }

    private boolean openFile() {
        try {
            File selectedFile = view.selectFile();
            model.readFile(selectedFile);
            view.getTableModel().fillTable(view.getColumnCount(), model.getHexString());
            view.setStartCursor();
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    private void closeFile() {
        model.clearData();
        view.getTableModel().clearTable();
        view.getStatusBarView().clearDataLabel();
    }

    private void saveFileAs() throws IOException {
        File selectedFile = view.selectFile();
        if (!selectedFile.exists()) {
            selectedFile.createNewFile();
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(selectedFile))) {
            for (int i = 0; i < view.getHexTable().getRowCount(); i++) {
                for (int j = 1; j < view.getHexTable().getColumnCount(); j++) {
                    bos.write(getBytesFromHex(view.getHexTable().getValueAt(i, j).toString()));
                }
            }
        }
    }

    class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private void updateSelectedDataLabel() {
        try {
            String selectedData = view.getSelectedData();
            byte[] bytes = getBytesFromHex(selectedData);

            if (selectedData.length() == 2) {
                view.getStatusBarView().updateIntegerValueLabelForByte(bytes);
                view.getStatusBarView().updateUnsignedIntegerValueLabel(selectedData);
            } else if (selectedData.length() == 4) {
                view.getStatusBarView().updateIntegerValueLabelForShort(bytes);
                view.getStatusBarView().updateUnsignedIntegerValueLabel(selectedData);
            } else if (selectedData.length() == 8) {
                view.getStatusBarView().updateIntegerValueLabelForInt(bytes);
                view.getStatusBarView().updateUnsignedIntegerValueLabel(selectedData);
            } else if (selectedData.length() == 16) {
                view.getStatusBarView().updateIntegerValueLabelForLong(bytes);
                view.getStatusBarView().updateUnsignedIntegerValueLabel(selectedData);
                view.getStatusBarView().updateFloatValueLabel(bytes);
                view.getStatusBarView().updateDoubleValueLabel(bytes);
            }
        } catch (RuntimeException runtimeException) {
            view.getStatusBarView().clearDataLabel();
        }
    }

    class FindListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchDialog.setVisible(true);
        }
    }

    class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Objects.equals(e.getActionCommand(), "Down")) {
                search(Direction.FORWARD);
            } else {
                search(Direction.BACK);
            }
        }

        private void search(Direction direction) {
            String searchText = searchDialog.getSearchText();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(searchDialog,
                        "Поле пустое",
                        "Поиск",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            try {
                Pattern pattern = Pattern.compile(searchText);
                JTable table = view.getHexTable();

                if (direction == Direction.FORWARD) {
                    searchForward(table, pattern);
                } else {
                    searchBack(table, pattern);
                }
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(searchDialog,
                        "Ничего не было найдено",
                        "Поиск",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        public Pair<Integer, Integer> getNextSelectCell() {
            int selectedRow = view.getHexTable().getSelectedRow();
            int selectedCol = view.getHexTable().getSelectedColumn();

            int nextSelectedRow, nextSelectedCol;
            if (selectedCol + 1 < view.getHexTable().getColumnCount()) {
                nextSelectedCol = selectedCol + 1;
                nextSelectedRow = selectedRow;
            } else if (selectedRow + 1 < view.getHexTable().getRowCount()) {
                nextSelectedCol = 1;
                nextSelectedRow = selectedRow + 1;
            } else {
                nextSelectedCol = 1;
                nextSelectedRow = 0;
            }
            return new Pair<>(nextSelectedRow, nextSelectedCol);
        }

        public Pair<Integer, Integer> getPreviousSelectCell() {
            int selectedRow = view.getHexTable().getSelectedRow();
            int selectedCol = view.getHexTable().getSelectedColumn();

            int nextSelectedRow, nextSelectedCol;
            if (selectedCol - 1 > 0) {
                nextSelectedCol = selectedCol - 1;
                nextSelectedRow = selectedRow;
            } else if (selectedRow - 1 > -1) {
                nextSelectedCol = view.getHexTable().getColumnCount() - 1;
                nextSelectedRow = selectedRow - 1;
            } else {
                nextSelectedCol = view.getHexTable().getColumnCount() - 1;
                nextSelectedRow = view.getHexTable().getRowCount() - 1;
            }
            return new Pair<>(nextSelectedRow, nextSelectedCol);
        }


        private void searchTest(JTable table, Pattern pattern, int startRow, int startCol, int rowIncrement, int colIncrement) {
            int rowCount = table.getRowCount();
            int colCount = table.getColumnCount();

            for (int row = startRow; row >= 0 && row < rowCount; row += rowIncrement) {
                for (int col = startCol; col >= 1 && col < colCount; col += colIncrement) {
                    String cellText = table.getValueAt(row, col).toString();
                    if (pattern.matcher(cellText).find()) {
                        table.setRowSelectionInterval(row, row);
                        table.setColumnSelectionInterval(col, col);
                        table.scrollRectToVisible(table.getCellRect(row, col, true));
                        return;
                    }
                }
            }

            throw new RuntimeException("Nothing found");
        }

        private void searchBack(JTable table, Pattern pattern) {
            Pair<Integer, Integer> nextSelectCell = getPreviousSelectCell();
            searchTest(table, pattern, nextSelectCell.getFirst(), nextSelectCell.getSecond(), -1, -1);
        }

        private void searchForward(JTable table, Pattern pattern) {
            Pair<Integer, Integer> nextSelectCell = getNextSelectCell();
            searchTest(table, pattern, nextSelectCell.getFirst(), nextSelectCell.getSecond(), 1, 1);
        }
    }

    class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    }

    private void delete() {
        int[] selectedRows = view.getHexTable().getSelectedRows();
        int[] selectedCols = view.getHexTable().getSelectedColumns();

        for (int selectRow : selectedRows) {
            for (int selectCol : selectedCols) {
                view.getHexTable().setValueAt("00", selectRow, selectCol);
            }
        }
    }

    class CopyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            copy();
        }
    }

    private void copy() {
        int[] selectedRows = view.getHexTable().getSelectedRows();
        int[] selectedCols = view.getHexTable().getSelectedColumns();

        // создаем буфер обмена
        StringSelection stringSelection = new StringSelection("");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // формируем строку с данными выделенных ячеек
        StringBuilder builder = new StringBuilder();
        for (int row : selectedRows) {
            for (int col : selectedCols) {
                builder.append(view.getHexTable().getValueAt(row, col)).append("\t");
            }
            builder.append("\n");
        }

        // помещаем строку в буфер обмена
        stringSelection = new StringSelection(builder.toString());
        clipboard.setContents(stringSelection, null);
    }

    class CutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            copy();
            delete();
        }
    }

    class PasteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTable table = view.getHexTable();
            // получаем буфер обмена
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = clipboard.getContents(null);

            // проверяем, что в буфере обмена содержится текст
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // получаем текст из буфера обмена
                    String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);

                    // разбиваем текст на строки и ячейки
                    String[] rows = text.split("\n");
                    for (int i = 0; i < rows.length; i++) {
                        String[] cells = rows[i].split("\t");

                        // вставляем значения ячеек в таблицу
                        for (int j = 0; j < cells.length; j++) {
                            int row = table.getSelectedRow() + i;
                            int col = table.getSelectedColumn() + j;
                            if (row < table.getRowCount() && col < table.getColumnCount()) {
                                table.setValueAt(cells[j], row, col);
                            }
                        }
                    }
                } catch (UnsupportedFlavorException | IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}

