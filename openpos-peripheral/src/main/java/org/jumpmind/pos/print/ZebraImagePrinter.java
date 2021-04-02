package org.jumpmind.pos.print;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ZebraImagePrinter {

    public String getZebraGraphicsCommand(BufferedImage image) {
        String printImageCommand = "";
        int color = 0,bit = 0,currentValue = 0,redValue = 0, blueValue = 0, greenValue = 0;

        int loopWidth = 8 - (image.getWidth() % 8); // Make sure the width is divisible by 8
        if (loopWidth == 8)
            loopWidth = image.getWidth();
        else
            loopWidth += image.getWidth();

        printImageCommand = "EG" + " " +
                Integer.toString((loopWidth / 8)) + " " +
                Integer.toString(image.getHeight()) + " " +
                0 + " " +
                0 + " ";

        for (int y = 0; y < image.getHeight(); y++) {
            bit = 128;
            currentValue = 0;
            for (int x = 0; x < loopWidth; x++) {
                int intensity = 0;

                if (x < image.getWidth()) {
                    Color c = new Color(image.getRGB(x, y));
                    redValue = c.getRed();
                    blueValue = c.getBlue();
                    greenValue = c.getGreen();

                    intensity = 255 - ((redValue + greenValue + blueValue) / 3);
                }
                else {
                    intensity = 0;
                }

                if (intensity >= 128) {
                    currentValue |= bit;
                }
                bit = bit >> 1;
                if (bit == 0) {
                    String hex = Integer.toHexString(currentValue);
                    hex = leftPad(hex);
                    printImageCommand = printImageCommand + hex.toUpperCase();
                    bit = 128;
                    currentValue = 0;
                }
            }
        }
        printImageCommand = printImageCommand + "\r\n";

        return printImageCommand;
    }

    private String leftPad(String s) {
        if (s.length() == 1)  {
            s = "0" + s;
        }
        return s;
    }
}
