package org.jumpmind.pos.core.ui.data;

import java.io.Serializable;

public class TenderItem implements Serializable {
    private String typeName;
    private String amount;

    public TenderItem(String type, String amount ){
        this.typeName = type;
        this.amount = amount;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
