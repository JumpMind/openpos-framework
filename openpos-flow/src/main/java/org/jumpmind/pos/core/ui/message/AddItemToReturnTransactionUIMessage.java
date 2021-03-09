package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class AddItemToReturnTransactionUIMessage extends UIMessage {
    ActionItem addItemButton;
    Form form;
}
