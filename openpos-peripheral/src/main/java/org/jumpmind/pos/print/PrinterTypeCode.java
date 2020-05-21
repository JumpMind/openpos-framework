package org.jumpmind.pos.print;

import org.jumpmind.pos.util.model.AbstractTypeCode;

public class PrinterTypeCode extends AbstractTypeCode {
    private static final long serialVersionUID = 1L;

    public static final PrinterTypeCode OPENPOS = new PrinterTypeCode("openpos");
    public static final PrinterTypeCode JAVAPOS = new PrinterTypeCode("javapos");

    public static PrinterTypeCode of(String value) {
        return AbstractTypeCode.of(PrinterTypeCode.class, value);
    }

    private PrinterTypeCode(String value) {
        super(value);
    }


}
