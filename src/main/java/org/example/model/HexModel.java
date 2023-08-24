package org.example.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HexModel {
    private String[] hexString;

    // Устанавливает hexString в модель
    public void readFile(File filePath) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            byte[] buffer = new byte[(int) raf.length()];
            raf.readFully(buffer);

            StringBuilder hexContent = new StringBuilder();
            for (byte b : buffer) {
                hexContent.append(String.format("%02X ", b));
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

