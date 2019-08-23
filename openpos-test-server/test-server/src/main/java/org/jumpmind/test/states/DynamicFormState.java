package org.jumpmind.test.states;


import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.OnArrive;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.DynamicFormScreen;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;

import java.util.ArrayList;
import java.util.List;

public class DynamicFormState {
    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnArrive
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    private UIMessage buildScreen() {
        DynamicFormScreen message = new DynamicFormScreen();
        message.setRefreshAlways(true);
        message.setBackButton(new ActionItem("Back", null));
        message.setSubmitButton(new ActionItem("Back", "Back"));
        message.setUseOnScreenKeyboard(true);

        Form form = message.getForm();
        List<String> comboValues = new ArrayList<>();

        for( int i = 0; i < 100; ++i){
            comboValues.add("TestValue"+i);
        }

        for( int i = 0; i < 3; ++i){
            form.addComboBox("testCombo" +1, "Test Combo" +1, comboValues);
        }


        for( int i = 0; i < 10; ++i){
            FormField input = form.addTextField("testField"+i, "Test Input"+i, "", false);
            input.setMaxLength(30);
        }


        return message;
    }
}