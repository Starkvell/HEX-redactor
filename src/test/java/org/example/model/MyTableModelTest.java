package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class MyTableModelTest {

    private File file;
    private MyTableModel model;

    @BeforeEach
    void setUp() {
        model = new MyTableModel();
        file = new File("target/test-classes/test.txt");
    }

    @Test
    void readFileAndLoadNewPieceOfData() throws IOException {
        String[] expected = new String[]{"68", "65", "6C", "6C", "6F", "20", "77", "6F", "72", "6C", "64"};

        String[] actualString = model.readFileAndLoadNewPieceOfData(file);

        assertArrayEquals(expected, actualString);
    }

    @Test
    void fillTable() {
        String[] hexString = new String[]{"68", "65", "6C", "6C", "6F", "20", "77", "6F", "72", "6C", "64"};
        String[] expected = new String[]{"0000", "68", "65", "6C", "6C", "6F", "20", "77", "6F", "72", "6C", "64", null, null, null, null, null};

        model.createTable(16);
        model.fillTable(16, hexString);
        Object[] vectorArray = model.getDataVector().elementAt(0).toArray();

        assertArrayEquals(expected, vectorArray);
    }

    @Test
    void createTable() {
        final int NUMBER_OF_COLUMNS = 16;
        String[] expectedColumnName = new String[]{"Offset", "0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "A", "B", "C", "D", "E", "F"};

        model.createTable(NUMBER_OF_COLUMNS);
        if (model.getColumnCount() == NUMBER_OF_COLUMNS + 1) {
            for (int i = 0; i < NUMBER_OF_COLUMNS + 1; i++) {
                String columnName = model.getColumnName(i);
                if (!Objects.equals(columnName, expectedColumnName[i])) {
                    fail();
                }
            }
        } else {
            fail();
        }
    }
}