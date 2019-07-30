package org.jumpmind.pos.core.screen;

import java.io.Serializable;

public class TransactionReceiptLine implements Serializable {

    private static final long serialVersionUID = -3667174964838343444L;

    private String label;
    private String value;

    public TransactionReceiptLine() {

    }

    public TransactionReceiptLine(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
