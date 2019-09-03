package org.jumpmind.pos.print;

import java.util.Map;

public class PrinterConfig {

    boolean enabled = true;
    PrinterTypeCode type;
    Map<String, Object> settings;
    String stylesSheet;

    public PrinterTypeCode getType() {
        return type;
    }

    public void setType(PrinterTypeCode type) {
        this.type = type;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public String getStylesSheet() {
        return stylesSheet;
    }

    public void setStylesSheet(String stylesSheet) {
        this.stylesSheet = stylesSheet;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
