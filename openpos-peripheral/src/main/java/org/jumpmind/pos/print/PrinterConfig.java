package org.jumpmind.pos.print;

import lombok.Data;

import java.util.Map;

@Data
public class PrinterConfig {

    boolean enabled = true;
    PrinterTypeCode type;
    Map<String, Object> settings;
    String styleSheet;
}
