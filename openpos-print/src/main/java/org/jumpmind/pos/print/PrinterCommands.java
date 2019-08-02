package org.jumpmind.pos.print;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PrinterCommands {

    public static final String ESC = "ESC";
    public static final String FORMAT_BOLD = "FORMAT_BOLD";
    public static final String FONT_SIZE_MEDIUM = "FONT_SIZE_MEDIUM";
    public static final String FONT_SIZE_LARGE = "FONT_SIZE_LARGE";
    public static final String LINE_SPACING_TIGHT = "LINE_SPACING_TIGHT";
    public static final String LINE_SPACING_SINGLE = "LINE_SPACING_SINGLE";
    public static final String LINE_SPACING_1_AND_HALF = "LINE_SPACING_1_AND_HALF";
    public static final String FORMAT_NORMAL = "FORMAT_NORMAL";
    public static final String CUT_PAPER = "CUT_PAPER";
    public static final String ALIGN_CENTER = "ALIGN_CENTER";
    public static final String ALIGN_LEFT = "ALIGN_LEFT";
    public static final String ESC_P_MODE = "ESC_P_MODE";
    public static final String PRINT_BARCODE = "PRINT_BARCODE";
    public static final String BARCODE_TYPE_CODE_128 = "BARCODE_TYPE_CODE_128";
    public static final String BARCODE_TYPE_CODE_128_CODEA = "BARCODE_TYPE_CODE_128_CODEA";
    public static final String IMAGE_START_BYTE = "IMAGE_START_BYTE";
    public static final String BARCODE_TYPE_CODE_39 = "BARCODE_TYPE_CODE_39";
    public static final String BARCODE_FEED = "BARCODE_FEED";

    private Map<String, String> printCommands;

    public void load(URL propertiesUrl) {
        Map<String, String> properties = loadProperties(propertiesUrl);
        properties = dereferenceProperties(properties);
        properties = toHex(properties);
        properties = toPrintCommands(properties);

        if (printCommands == null) {
            printCommands = new HashMap<>();
        }
        printCommands.putAll(properties);
    }

    protected Map<String, String> loadProperties(URL propertiesUrl) {
        try {
            Properties props = new Properties();
            System.out.println("Loading printer command properties from: " + propertiesUrl);
            props.load(propertiesUrl.openStream());

            Map<String, String> properties = new HashMap<>();

            for (Object key : props.keySet()) {
                properties.put(key.toString(), props.get(key).toString());
            }
            return properties;
        } catch (Exception ex) {
            throw new PrintException("Failed to load properties from " + propertiesUrl, ex);
        }
    }

    protected Map<String, String> dereferenceProperties(Map<String, String> properties) {
        Map<String, String> dereferencedProperties = new HashMap<>();
        for (String key : properties.keySet()) {
            String[] parts = splitEntry(properties.get(key));
            for (int i = 0; i < parts.length; i++) {
                if (properties.containsKey(parts[i])) {
                    parts[i] = properties.get(parts[i]);
                }

            }
            dereferencedProperties.put(key, StringUtils.join(parts, ','));
        }
        return dereferencedProperties;
    }

    protected Map<String, String> toHex(Map<String, String> properties) {
        Map<String, String> dereferencedProperties = new HashMap<>();
        for (String key : properties.keySet()) {
            String[] parts = splitEntry(properties.get(key));
            for (int i = 0; i < parts.length; i++) {
                if (isDigits(parts[i])) {
                    int code = Integer.parseInt(parts[i]);
                    String hex = "0x" + stringToHexString(String.valueOf((char)code));
                    parts[i] = hex;
                }

            }
            dereferencedProperties.put(key, StringUtils.join(parts, ','));
        }
        return dereferencedProperties;
    }

    protected Map<String, String> toPrintCommands(Map<String, String> properties) {
        Map<String, String> printCommandsLocal = new HashMap<>();
        for (String key : properties.keySet()) {
            String[] parts = splitEntry(properties.get(key));
            String printCommand = "";
            for (int i = 0; i < parts.length; i++) {
                if (isHex(parts[i])) {
                    int code = Integer.parseInt(parts[i].substring(2), 16);
                    printCommand += String.valueOf((char) code);
                } else {
                    printCommand += parts[i];
                }

            }
            printCommandsLocal.put(key, printCommand);
        }
        return printCommandsLocal;
    }

    protected String[] splitEntry(String entry) {
        String[] parts = entry.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    protected boolean isDigits(String s) {
        return NumberUtils.isDigits(s);
    }

    protected boolean isHex(String s) {
        return s != null && s.startsWith("0x");
    }

    public String get(String name) {
        return printCommands.get(name);
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





}
