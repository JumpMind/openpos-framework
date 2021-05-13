package org.jumpmind.pos.core.flow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.model.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StateManagerDoubleExistSubTest {
    
    @InjectMocks
    Injector injector;

    @Mock
    IMessageService messageService;

    @Test
    public void testDoubleSubTransitionExit() throws Exception {

        doNothing().when(messageService).sendMessage(any(String.class), any(Message.class));

        StateManager stateManager = StateManagerTestUtils.buildStateManager(injector, "testflows/test-double-sub-exit-flow.yml");
        
        assertEquals(OrderDetailsState.class, stateManager.getCurrentState().getClass());

        StateManagerTestUtils.doAction(stateManager, "BagPromptRequired");
        
        assertEquals(BagScanState.class, stateManager.getCurrentState().getClass());

        StateManagerTestUtils.doAction(stateManager, "BagNotFound");
        
        assertEquals(BagNotFoundState.class, stateManager.getCurrentState().getClass());

        StateManagerTestUtils.doAction(stateManager, "NoBagsAdded");
        
        assertEquals(OrderDetailsState.class, stateManager.getCurrentState().getClass());
    }
    
    public static class OrderDetailsState  {
        @OnArrive
        public void arrive(Action action) {
        }
        
        @ActionHandler
        public void onNoBagsAdded(Action action) {
            
        }
    }

    public static class BagScanState  {
        @OnArrive
        public void arrive(Action action) {
        }
    }

    public static class BagNotFoundState  {
        @OnArrive
        public void arrive(Action action) {
        }
    }    
     
    
    
}
