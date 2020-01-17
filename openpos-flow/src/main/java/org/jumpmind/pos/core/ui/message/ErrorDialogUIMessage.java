package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class ErrorDialogUIMessage extends UIMessage {

    String title;
    String message;
    ActionItem button;

    @Builder
    public ErrorDialogUIMessage(ActionItem button, String message, String title) {
        super("ErrorDialog", "ErrorDialog");
        this.title = title;
        this.message = message;
        this.button = button;
        asDialog();
    }
}
