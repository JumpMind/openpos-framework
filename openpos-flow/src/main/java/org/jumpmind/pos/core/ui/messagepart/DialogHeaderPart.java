package org.jumpmind.pos.core.ui.messagepart;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasBackButton;

import java.io.Serializable;

@Data
public class DialogHeaderPart implements IHasBackButton, Serializable {
    private static final long serialVersionUID = 1L;

    private String headerText;
    private String headerIcon;
    private ActionItem backButton;

    public DialogHeaderPart() {
    }

    public DialogHeaderPart(String headerText) {
        this(headerText, null, null);
    }

    public DialogHeaderPart(String headerText, ActionItem backButton) {
        this(headerText, null, backButton);
    }

    public DialogHeaderPart(String headerText, String headerIcon) {
        this(headerText, headerIcon, null);
    }
    
    public DialogHeaderPart(String headerText, String headerIcon, ActionItem backButton) {
        this.headerText = headerText;
        this.headerIcon = headerIcon;
        this.backButton = backButton;
    }    
}
