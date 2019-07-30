package org.jumpmind.pos.print;


public class EscP_Constants {

//    public static final int ESC = 0x1b;

    public static final int ESC = 27;
    public static final int CHANGE_MODE = 0x21;
    private static final int ALIGN = 97;
    private static final int CENTER = 1;
    private static final int LEFT = 0;

    public static final String ESP_P_MODE = codes(ESC, 105, 97, 0);

    public static final String FORMAT_NORMAL = codes(ESC, CHANGE_MODE, 0x0);
    public static final String FORMAT_BOLD = codes(ESC, CHANGE_MODE,0x8);

    public static final String FONT_LETTER_GOTHIC = codes(ESC, 107, 9);
    public static final String FONT_SIZE_MEDIUM = codes(ESC, 88, 1, 25, 1);
    public static final String FONT_SIZE_LARGE = codes(ESC, 88, 1, 64, 0);
    public static final String FONT_SIZE_LARGE_EPSON = codes(29, 33, 0b00010001);

    public static final String LINE_SPACING_SINGLE = codes(ESC,51, 30);
    public static final String LINE_SPACING_TIGHT = codes(ESC,51, 25);
    public static final String LINE_SPACING_1_AND_HALF = codes(ESC,51, 45);

    public static final String LINE_SPACING_TIGHT_EPSON = codes(ESC,51, 50);
    public static final String LINE_SPACING_SINGLE_EPSON = codes(ESC,51, 60);
    public static final String LINE_SPACING_1_AND_HALF_EPSON = codes(ESC,51, 80);

    public static final String ALIGN_CENTER = codes(ESC,ALIGN, CENTER);
    public static final String ALIGN_LEFT = codes(ESC,ALIGN, LEFT);

    public static final String EPSON_CODE128_BARCODE = String.valueOf((char)0x1D)+String.valueOf((char)0x48)+String.valueOf((char)2)+
            String.valueOf((char)0x1D)+String.valueOf((char)0x68)+String.valueOf((char)90)+
            String.valueOf((char)0x1D)+String.valueOf((char)0x77)+String.valueOf((char)2)+
            String.valueOf((char)0x1D)+String.valueOf((char)0x6B)+String.valueOf((char)73)+
            String.valueOf((char)23)+
            String.valueOf((char)123)+ String.valueOf((char)66)+ "380502001835720192324"+
            String.valueOf((char)0);



//    public static final String BARCODE_PARAMETERS = "i";
////    public static final String BARCODE_TYPE_CODE39 = "t0";
////    public static final String BARCODE_DATA_START = "B";
////    public static final String BARCODE_DATA_END = "\\";

    private static String codes(int... codes) {
        String s = "";
        for (int i : codes) {
            s += String.valueOf((char)i);
        }
        return s;
    }




}
