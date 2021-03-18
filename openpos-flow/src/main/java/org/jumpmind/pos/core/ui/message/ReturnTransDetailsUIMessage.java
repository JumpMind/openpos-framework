package org.jumpmind.pos.core.ui.message;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.jumpmind.pos.core.model.CheckboxField;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.data.SellItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class ReturnTransDetailsUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;
    
    public ReturnTransDetailsUIMessage() {
        setId("returnTransactionDetails");
        setScreenType("ReturnTransDetailDialog");
        additionalButtons = new ArrayList<>();
    }

    String instructions;
    List<SellItem> items;
    CheckboxField employeeTransaction;
    List<ActionItem> additionalButtons;
    ActionItem selectionButton;
}
