package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.flow.ApplicationState;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.util.LogFormatter;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IMessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;

@RunWith(MockitoJUnitRunner.class)
public class ScreenServiceTest {
    
    @Mock
    IStateManagerContainer stateMgrCtr;

    @Mock
    IStateManager stateMgr;

    @Mock
    IMessageService messageService;

    @Mock
    UIDataMessageProviderService uiDataMessageProviderService;

    @InjectMocks
    ScreenService screenService;
    
    @Before
    public void beforeTest() {
        screenService.logFormatter = new LogFormatter();
    }

    /**
     * Confirms bugfix for case when a UIMessage with a Form was the last screen
     * sent from server to client and the client returns an action without the form.
     * In this case the ScreenService should not (mis)convert the action to a Form.
     */
    @Test
    public void test_Action_sent_withLinkedHashMap_onMessageWithForm() {
        when(stateMgrCtr.retrieve(anyString())).thenReturn(stateMgr);
        ApplicationState appState = new ApplicationState();
        appState.setLastScreen(new DummyFormUIMessage());
        when(stateMgr.getApplicationState()).thenReturn(appState);
        
        LinkedHashMap<String, Object> customActionData = new LinkedHashMap<>();
        customActionData.put("attr1", "value1");
        Action action = new Action("DoSomething", customActionData);
        screenService.actionOccurred("00000-000", action);
        
        assertNotNull(action.getData());
        assertFalse(action.getData() instanceof Form);
    }

    
    public class DummyFormUIMessage extends UIMessage implements IHasForm {
        private static final long serialVersionUID = 1L;

        Form f;
        
        public DummyFormUIMessage() {
            f = new Form();
            f.addTextField("field1", "Name", "Joe", true);
        }
        
        @Override
        public void setForm(Form form) {
            this.f = form;
        }

        @Override
        public Form getForm() {
            return this.f;
        }
        
    }
}
