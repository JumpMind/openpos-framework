package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.IHasForm;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@Data
public class DynamicFormUIMessage extends UIMessage implements IHasForm {

    private Form form = new Form();

    private ActionItem submitButton;

    private String instructions;

    private List<String> alternateSubmitActions = new ArrayList<String>();

    private String imageUrl;

    public DynamicFormUIMessage() {
        setScreenType(UIMessageType.DYNAMIC_FORM);
    }

}
