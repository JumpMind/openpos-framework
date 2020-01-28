package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;

@Data
@NoArgsConstructor
public class ErrorDialogUIMessage extends UIMessage {

    String message;
    String imageUrl;
    String altImageUrl;
    ActionItem button;

    @Builder
    public ErrorDialogUIMessage(ActionItem button, String message, String title, String imageUrl) {
        super("ErrorDialog", "ErrorDialog");
        DialogHeaderPart header = new DialogHeaderPart();
        header.setHeaderText(title);
        this.addMessagePart(MessagePartConstants.DialogHeader, header);
        this.message = message;
        this.button = button;
        this.imageUrl = imageUrl;
        asDialog();
    }


}
