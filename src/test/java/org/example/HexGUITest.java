package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class HexGUITest {
    @Test
    public void getBytesFromHex() {
        String hexData = "48656c6c6f20576f726c64";
        byte[] expectedBytes = {72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100};

        byte[] actualBytes = HexGUI.getBytesFromHex(hexData);

        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    public void getBytesFromHex_empty() {
        String hexData = "";
        byte[] expectedBytes = {};

        byte[] actualBytes = HexGUI.getBytesFromHex(hexData);

        assertArrayEquals(expectedBytes, actualBytes);
    }
}
