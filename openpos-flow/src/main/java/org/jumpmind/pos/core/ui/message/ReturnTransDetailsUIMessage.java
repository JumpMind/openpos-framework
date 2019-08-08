package org.jumpmind.pos.core.ui.message;

import java.util.List;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.data.SellItem;
import org.jumpmind.pos.core.ui.UIMessage;

public class ReturnTransDetailsUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;
    
    public ReturnTransDetailsUIMessage() {
        setId("returnTransactionDetails");
        setScreenType("ReturnTransDetailDialog");
    }

    String instructions;
    List<SellItem> items;
    ActionItem selectionButton;
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setItems(List<SellItem> items) {
        this.items = items;
    }
    
    public List<SellItem> getItems() {
        return items;
    }
    
    public void setSelectionButton(ActionItem selectionButton) {
        this.selectionButton = selectionButton;
    }
    
    public ActionItem getSelectionButton() {
        return selectionButton;
    }
    
}
