package org.example;

import javax.swing.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class StatusBar extends JPanel{
    private JLabel integerLabel;
    private JLabel integerValueLabel;
    private JLabel unsignedIntegerLabel;
    private JLabel unsignedIntegerValueLabel;
    private JLabel floatLabel;
    private JLabel floatValueLabel;
    private JLabel doubleLabel;
    private JLabel doubleValueLabel;
    private JPanel StatusPanel;


    public void clearDataLabel() {
        integerValueLabel.setText("");
        unsignedIntegerValueLabel.setText("");
        floatValueLabel.setText("");
        doubleValueLabel.setText("");
    }

    public void updateDoubleValueLabel(byte[] bytes) {
        double doubleValue = ByteBuffer.wrap(bytes).getDouble();
        doubleValueLabel.setText(String.valueOf(doubleValue));
    }

    public void updateFloatValueLabel(byte[] bytes) {
        float floatValue = ByteBuffer.wrap(bytes).getFloat();
        floatValueLabel.setText(String.valueOf(floatValue));
    }

    public void updateIntegerValueLabelForLong(byte[] bytes) {
        long integerValue = ByteBuffer.wrap(bytes).getLong();
        integerValueLabel.setText(String.valueOf(integerValue));
    }

    public void updateIntegerValueLabelForInt(byte[] bytes) {
        int integerValue = ByteBuffer.wrap(bytes).getInt();
        integerValueLabel.setText(String.valueOf(integerValue));
    }

    public void updateIntegerValueLabelForShort(byte[] bytes) {
        short shortValue = ByteBuffer.wrap(bytes).getShort();
        integerValueLabel.setText(String.valueOf(shortValue));
    }

    public void updateIntegerValueLabelForByte(byte[] bytes) {
        byte byteValue = ByteBuffer.wrap(bytes).get();
        integerValueLabel.setText(String.valueOf(byteValue));
    }

    public void updateUnsignedIntegerValueLabel(String stringData) {
        BigInteger bigInteger = new BigInteger(stringData, 16);
        unsignedIntegerValueLabel.setText(bigInteger.toString());
    }
}



