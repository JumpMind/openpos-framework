package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.startup.DeviceStartupTaskConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;

@RunWith(ConcurrentTestRunner.class)
public class StateManagerContainerTest {

    StateManagerContainer container = new StateManagerContainer();
    
    @Before
    public void setup() throws Exception {
        final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        final StateManager stateManager = Mockito.mock(StateManager.class);
        final IFlowConfigProvider flowConfigProvider = Mockito.mock(IFlowConfigProvider.class);
        ClientContext clientContext = Mockito.mock(ClientContext.class);

        Mockito.when(applicationContext.getBean(StateManager.class)).thenReturn(stateManager);
        Mockito.when(applicationContext.getBeanNamesForType(ITransitionStep.class)).thenReturn(new String[0]);
        TestUtil.setField(container, "applicationContext", applicationContext);
        TestUtil.setField(container, "flowConfigProvider", flowConfigProvider);
        TestUtil.setField(container, "clientContext", clientContext);
        
        for (int i = 0; i < 50; i++) {            
            container.create("pos", "0000-"+i, null, null);
        }
    }
    
    @Test
    @ThreadCount(5)
    public void testConcurrentAccess() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 50; j++) {
                container.create("pos", "0000-"+i, null, null);
                container.retrieve("0000-"+j);
                container.removeSessionIdVariables("blah");
                container.getAllStateManagers();
                container.remove("0000-"+j);
            }
        }
    }
}
