package org.jumpmind.pos.core.model;

public class ScanData {
    private OpenposBarcodeType type;
    private String data;

    public OpenposBarcodeType getType() {
        return type;
    }

    public void setType(OpenposBarcodeType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
