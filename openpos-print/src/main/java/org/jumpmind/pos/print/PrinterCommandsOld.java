package org.jumpmind.pos.print;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.net.URL;
import java.util.*;

public class PrinterCommandsOld {

    public static final String ESC = "ESC";
    public static final String FORMAT_BOLD = "FORMAT_BOLD";
    public static final String FONT_SIZE_MEDIUM = "FONT_SIZE_MEDIUM";
    public static final String LINE_SPACING_TIGHT = "LINE_SPACING_TIGHT";
    public static final String LINE_SPACING_SINGLE = "LINE_SPACING_SINGLE";
    public static final String LINE_SPACING_1_AND_HALF = "LINE_SPACING_1_AND_HALF";
    public static final String FORMAT_NORMAL = "FORMAT_NORMAL";
    public static final String CUT_PAPER = "CUT_PAPER";
    public static final String LARGE_TEXT = "LARGE_TEXT";
    public static final String ALIGN_CENTER = "ALIGN_CENTER";
    public static final String ALIGN_LEFT = "ALIGN_LEFT";
    public static final String ESP_P_MODE = "ESP_P_MODE";

    Map<String, String> properties = new HashMap<>();

    public void load(URL url) {
        Properties props = new Properties();
        try {
            System.out.println("Loading printer command properties from: " + url);
            props.load(url.openStream());

            Map<String, String> loadingProperties = new HashMap<>();

            // first pass: populate
            for (Object keyObject : props.keySet()) {
                String key = (String) keyObject;
                String command = (String) props.get(keyObject);
                loadingProperties.put(key, command);
            }

            System.out.println("*** After load");
            for (Object keyObject : loadingProperties.keySet()) {
                System.out.println(keyObject + "=" + loadingProperties.get(keyObject));
            }

            // second pass: decimals converted to hex
            for (Object keyObject : loadingProperties.keySet()) {
                String key = (String) keyObject;
                String command = hexToDecimal((String) loadingProperties.get(keyObject));
                loadingProperties.put(key, command);
            }

            System.out.println("*** After hexToDecimal");
            for (Object keyObject : loadingProperties.keySet()) {
                System.out.println(keyObject + "=" + loadingProperties.get(keyObject));
            }

            // third pass: substitusions
            for (Object keyObject : loadingProperties.keySet()) {
                String key = (String) keyObject;
                String command = substitusions(loadingProperties.get(keyObject), loadingProperties);
                loadingProperties.put(key, command);
            }

            System.out.println("*** After replace substitusions");
            for (Object keyObject : loadingProperties.keySet()) {
                System.out.println(keyObject + "=" + loadingProperties.get(keyObject).toString());
            }

            // fourth pass: parse
            for (Object keyObject : loadingProperties.keySet()) {
                String key = (String) keyObject;
                String command = parseCommand(loadingProperties.get(keyObject), true, loadingProperties);
                loadingProperties.put(key, command);
            }

            System.out.println("*** After parse");
            for (Object keyObject : loadingProperties.keySet()) {
                System.out.println(keyObject + "=" + stringToHexString(loadingProperties.get(keyObject).toString()));
            }

            properties.putAll(loadingProperties);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load printer command properties from " + url, ex);
        }

    }

    protected String substitusions(String s, Map<String, String> currentlyLoadingProperties) {
        return parseCommand(s, false, currentlyLoadingProperties);
    }

    protected boolean isDigits(String s) {
        return NumberUtils.isDigits(s);
    }

    protected String hexToDecimal(String command) {
        String[] parts = command.split(",");
        StringBuilder modifiedCommand = new StringBuilder(64);
        for (String part : parts) {
            part = part.trim();
            if (isDigits(part)) {
                int code = Integer.parseInt(part);
                modifiedCommand.append("0x");
                modifiedCommand.append(stringToHexString(String.valueOf((char)code)));
                modifiedCommand.append(",");
            } else {
                modifiedCommand.append(part);
                modifiedCommand.append(",");
            }
        }

        modifiedCommand.setLength(modifiedCommand.length()-1);
        return modifiedCommand.toString();
    }

    public String parseCommand(String command, boolean parseHex, Map<String, String> currentlyLoadingProperties) {
        StringBuilder result = new StringBuilder();

        String[] parts = command.split(",");
        for (String part : parts) {
            part = parseCommandPart(part.trim(), parseHex, currentlyLoadingProperties);
            result.append(part);
            if (!parseHex) {
                result.append(",");
            }
        }

        if (!parseHex) {
            result.setLength(result.length()-1);
        }

        return result.toString();
    }

    protected String parseCommandPart(String commandPart, boolean parseHex, Map<String, String> currentlyLoadingProperties) {
        if (isHex(commandPart) && parseHex) {
            int code = Integer.parseInt(commandPart.substring(2), 16);
            return String.valueOf((char) code);
        } else {
            String referencedValue = properties.get(commandPart);
            if (referencedValue == null) {
                referencedValue = currentlyLoadingProperties.get(commandPart);
            }

            if (referencedValue != null) {
                return parseCommandPart(referencedValue.trim(), parseHex, currentlyLoadingProperties); // resurse
            } else {
                return commandPart;
            }
        }
    }

    public String get(String name) {
        return properties.get(name);
    }

    private boolean isHex(String s) {
        return s != null && s.startsWith("0x");
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String stringToHexString(String s) {
        byte[] bytes = s.getBytes();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexChars.length; i++) {
            result.append(hexChars[i++]);
            result.append(hexChars[i]);
            result.append(" ");
        }

        if (result.length() > 1) {
            result.setLength(result.length()-1);
        }

        return result.toString();
    }

    // Temp

    public static final int ESC_COMMAND = 27;
    public static final String LINE_SPACING_SINGLE_STRING = codes(ESC_COMMAND,51, 30);


    private static String codes(int... codes) {
        String s = "";
        for (int i : codes) {
            s += String.valueOf((char)i);
        }
        return s;
    }


}
