package org.example.controller;

import org.example.ColumnCountInputDialog;
import org.example.HexGUI;
import org.example.model.HexModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.example.HexGUI.getBytesFromHex;

public class HexController {
    private HexModel model;
    private HexGUI view;

    public HexController(HexModel model, HexGUI view) {
        this.model = model;
        this.view = view;

        view.getMenuManager().getFileMenuManager().addOpenFileListener(new OpenFileListener());
        view.getMenuManager().getFileMenuManager().addCloseFileListener(new CloseFileListener());
        view.getMenuManager().getFileMenuManager().addSaveFileAsListener(new SaveFileAsListener());
        view.getMenuManager().getEditMenuManager().addChangeColumnCountListener(new ChangeColumnCountListener());
        view.getMenuManager().getFileMenuManager().addExitListener(new ExitListener());
        view.setListSelectionModelListener(new TableSelectionModelListener());
    }

    class OpenFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.getStatusBarView().clearDataLabel();
            if (openFile()) {
                view.getMenuManager().getFileMenuManager().enableSaveAsButton(true);
                view.getMenuManager().getFileMenuManager().enableCloseFileButton(true);
                view.getMenuManager().getFileMenuManager().enableOpenFileButton(false);
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
        }
    }

    private class SaveFileAsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFileAs();
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
                    view.createTable(view.getColumnCount());
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
            if (e.getValueIsAdjusting()) {
                view.getStatusBarView().clearDataLabel();
                updateSelectedDataLabel();
            }
        }
    }

    private boolean openFile() {
        try {
            File selectedFile = view.selectFile();
            model.readFile(selectedFile);
            view.createTable(view.getColumnCount());
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    private void closeFile() {
        model.clearData();
        view.clearTable();
        view.getStatusBarView().clearDataLabel();
    }

    private void saveFileAs() {
        File selectedFile = null;
        try {
            selectedFile = view.selectFile();
            if (!selectedFile.exists()) {
                selectedFile.createNewFile();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Упс, файл не был сохранен",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(selectedFile))) {
            for (int i = 0; i < view.getHexTable().getRowCount(); i++) {
                for (int j = 1; j < view.getHexTable().getColumnCount(); j++) {
                    bos.write(getBytesFromHex(view.getHexTable().getValueAt(i, j).toString()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        } catch (RuntimeException runtimeException){
            view.getStatusBarView().clearDataLabel();
        }
    }
}

