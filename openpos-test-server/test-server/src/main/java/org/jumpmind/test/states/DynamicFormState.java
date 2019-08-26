package org.jumpmind.test.states;

import java.util.ArrayList;
import java.util.List;
import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.model.IFormElement;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IconType;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.message.DynamicFormUIMessage;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.server.model.Action;


public class DynamicFormState {
    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnArrive
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    private UIMessage buildScreen() {
        DynamicFormUIMessage message = new DynamicFormUIMessage();
        BaconStripPart bacon = new BaconStripPart();
        bacon.setHeaderIcon(IconType.AddCustomer);
        bacon.setHeaderText("Loading Screen Test");
        bacon.setBackButton(new ActionItem("Back", null));

        message.addMessagePart(MessagePartConstants.BaconStrip, bacon);

        Form form = message.getForm();
        List<String> comboValues = new ArrayList<>();

        for( int i = 0; i < 100; ++i){
            comboValues.add("TestValue"+i);
        }

        form.addComboBox("testCombo", "Test Combo", comboValues);
        FormField input = form.addTextField("testField", "Test Input", "", false);
        input.setMaxLength(30);

        return message;
    }
}
