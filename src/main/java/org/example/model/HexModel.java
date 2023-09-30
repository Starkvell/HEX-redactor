package org.example.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HexModel { //TODO Удалить этот класс, перенести все в MyTableModel
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

}

