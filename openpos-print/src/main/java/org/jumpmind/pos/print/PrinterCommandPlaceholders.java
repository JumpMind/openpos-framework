package org.jumpmind.pos.print;

public class PrinterCommandPlaceholders extends PrinterCommands {

    @Override
    public String get(String name) {
        return "<" + name + "/>";
    }

}
