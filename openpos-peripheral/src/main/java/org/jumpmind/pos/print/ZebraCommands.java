package org.jumpmind.pos.print;

public class ZebraCommands {

    // <!> {offset} <200> <200> {height} {qty}
    //where:
    //<!>: Use ‘!’ to begin a control session.
    //{offset}:The horizontal offset for the entire label. This value causes all fields to be offset horizontally
    //by the specified number of UNITS.
    //<200>:Horizontal resolution (in dots-per-inch).
    //<200>:Vertical resolution (in dots-per-inch).
    //{height}:The maximum height of the label.
    public static final String COMMAND_CPCL_MODE = "! 0 200 200 200 1";
    public static final String COMMAND_CPCL_MODE_Y = "! 0 200 200 %s 1";

    public static final String COMMAND_PRINT = "PRINT";
    public static final String COMMAND_ENABLE_LINE_PRINT = "! U1 setvar \"device.languages\" \"line_print\"\r\n";
    public static final String COMMAND_LINE_PRINT = "! U1 SETLP 7 0 24\r\n";
    public static final String COMMAND_PRINT_BARCODE = "! U1 CENTER\r\n! U1 B 128 1 2 100 0 0 %s\r\n\n\n\n";
    public static final String COMMAND_BOLD = "! U1 SETBOLD 2\r\n";
    public static final String COMMAND_NORMAL = "! U1 SETBOLD 0\r\n";
    public static final String COMMAND_FONT_SIZE_MEDIUM = COMMAND_LINE_PRINT;
    public static final String COMMAND_FONT_SIZE_LARGE = "! U1 SETLP 7 1 48\r\n";
    public static final String CENTER_HINT = "<center/>";

}
