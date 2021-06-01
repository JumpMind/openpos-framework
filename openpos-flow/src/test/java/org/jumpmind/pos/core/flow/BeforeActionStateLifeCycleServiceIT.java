package org.jumpmind.pos.core.flow;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;


import net.bytebuddy.implementation.bytecode.Throw;
import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.flow.TestStates.HomeState;
import org.jumpmind.pos.core.flow.TestStates.StateWithBeforeActionMethod;
import org.jumpmind.pos.core.flow.TestStates.StateWithBeforeActionMethodThatThrowsException;
import org.jumpmind.pos.core.flow.TestStates.StateWithMultipleBeforeActionAndFailOnExceptionIsFalse;
import org.jumpmind.pos.core.flow.TestStates.StateWithMultipleBeforeActionAndFailOnExceptionIsTrue;
import org.jumpmind.pos.core.flow.TestStates.StateWithMultipleBeforeActionMethods;

import org.jumpmind.pos.core.flow.config.FlowBuilder;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.model.Message;
import org.jumpmind.pos.util.startup.DeviceStartupTaskConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(MockitoJUnitRunner.class)
public class BeforeActionStateLifeCycleServiceIT {

    StateManager stateManager;

    @InjectMocks
    Injector injector;
    
    @Mock
    private IMessageService messageService;

    @Mock
    private DeviceStartupTaskConfig deviceStartupTaskConfig;
    
    @Before
    public void setUp() throws Exception {
        doNothing().when(messageService).sendMessage(any(String.class), any(Message.class));

        FlowConfig config = new FlowConfig();
        config.setInitialState(FlowBuilder.addState(HomeState.class)
            .withTransition("TestSingleBeforeActionMethod", StateWithBeforeActionMethod.class)
            .withTransition("TestMultipleBeforeActionMethods", StateWithMultipleBeforeActionMethods.class)
            .withTransition("TestMultipleBeforeActionMethodsAndFailOnExceptionIsFalse", StateWithMultipleBeforeActionAndFailOnExceptionIsFalse.class)
            .withTransition("TestMultipleBeforeActionMethodsAndFailOnExceptionIsTrue", StateWithMultipleBeforeActionAndFailOnExceptionIsTrue.class)
            .withTransition("TestSingleBeforeActionMethodThatThrowsException", StateWithBeforeActionMethodThatThrowsException.class)
            .build()
        );

        stateManager = StateManagerTestUtils.buildStateManager(injector, null);
        
        LocaleMessageFactory localeMessageFactory = new LocaleMessageFactory();
        TestUtil.setField(localeMessageFactory, "supportedLocales", new String[] {"en_US"});
        TestUtil.setField(stateManager, "localeMessageFactory", localeMessageFactory);
        
//        ActionHandlerImpl actionHandler = new ActionHandlerImpl();
//        actionHandler.setActionHandlerHelper(new ActionHandlerHelper());
//
//        actionHandler.setBeforeActionService(new BeforeActionStateLifecycleService());
        stateManager.setInitialFlowConfig(config);
        TestUtil.setField(stateManager, "injector", injector);
    }

    @Test
    public void testSingleBeforeActionMethod() {
        stateManager.init("pos", "100-1");
        StateManagerTestUtils.doAction(stateManager, "TestSingleBeforeActionMethod");

        StateWithBeforeActionMethod testState = (StateWithBeforeActionMethod) stateManager.getCurrentState();
        assertFalse(testState.beforeActionInvoked);
        assertFalse(testState.onAction1Invoked);
        StateManagerTestUtils.doAction(stateManager, "Action1");
        assertTrue(testState.beforeActionInvoked);
        assertTrue(testState.onAction1Invoked);
    }

    @Test(expected=FlowException.class)
    public void testSingleBeforeActionMethodThatThrowsException() throws Throwable {
        stateManager.init("pos", "100-1");
        StateManagerTestUtils.doAction(stateManager, "TestSingleBeforeActionMethodThatThrowsException");
        StateWithBeforeActionMethodThatThrowsException testState = (StateWithBeforeActionMethodThatThrowsException) stateManager.getCurrentState();
        assertFalse(testState.beforeActionInvoked);
        assertFalse(testState.onAction1Invoked);

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();

        final AtomicReference<Throwable> errorHandlerCalled = new AtomicReference<>();

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    errorHandlerCalled.set(ex);
                }
            });

            StateManagerTestUtils.doAction(stateManager, "Action1");
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }

        if (errorHandlerCalled.get() != null) {
            throw errorHandlerCalled.get();
        }
    }
    
    @Test
    public void testMultipleBeforeActionMethods() {
        stateManager.init("pos", "100-1");
        StateManagerTestUtils.doAction(stateManager, "TestMultipleBeforeActionMethods");
        StateWithMultipleBeforeActionMethods testState = (StateWithMultipleBeforeActionMethods) stateManager.getCurrentState();
        assertFalse(testState.beforeAction_AInvoked);
        assertFalse(testState.beforeAction_BInvoked);
        assertFalse(testState.beforeAction_CInvoked);
        assertFalse(testState.onAction1Invoked);
        StateManagerTestUtils.doAction(stateManager, "Action1");
        
        assertTrue(testState.beforeAction_AInvoked);
        assertTrue(testState.beforeAction_BInvoked);
        assertTrue(testState.beforeAction_CInvoked);
        assertTrue(testState.onAction1Invoked);
    }
    
    @Test
    public void testMultipleBeforeActionMethodsAndFailOnExceptionIsFalse() {
        stateManager.init("pos", "100-1");
        StateManagerTestUtils.doAction(stateManager, "TestMultipleBeforeActionMethodsAndFailOnExceptionIsFalse");
        StateWithMultipleBeforeActionAndFailOnExceptionIsFalse testState = (StateWithMultipleBeforeActionAndFailOnExceptionIsFalse) stateManager.getCurrentState();
        assertFalse(testState.beforeAction_AInvoked);
        assertFalse(testState.beforeAction_BInvoked);
        assertFalse(testState.beforeAction_CInvoked);
        assertFalse(testState.beforeAction_DInvoked);
        assertFalse(testState.onAction1Invoked);
        // StateWithMultipleBeforeActionAndFailOnExceptionIsFalse will check that beforeAction methods are fired in correct order
        StateManagerTestUtils.doAction(stateManager, "Action1");
        
        assertTrue(testState.beforeAction_AInvoked);
        assertTrue(testState.beforeAction_BInvoked);
        assertTrue(testState.beforeAction_CInvoked);
        assertFalse(testState.beforeAction_DInvoked);
        assertTrue(testState.onAction1Invoked);
    }

    @Test
    public void testMultipleBeforeActionMethodsAndFailOnExceptionIsTrue() {
        stateManager.init("pos", "100-1");
        StateManagerTestUtils.doAction(stateManager, "TestMultipleBeforeActionMethodsAndFailOnExceptionIsTrue");
        StateWithMultipleBeforeActionAndFailOnExceptionIsTrue testState = (StateWithMultipleBeforeActionAndFailOnExceptionIsTrue) stateManager.getCurrentState();
        assertFalse(testState.beforeAction_AInvoked);
        assertFalse(testState.beforeAction_BInvoked);
        assertFalse(testState.beforeAction_CInvoked);
        assertFalse(testState.beforeAction_DInvoked);
        assertFalse(testState.onAction1Invoked);

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();

        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    assertNotNull(ex);
                    assertFalse(testState.beforeAction_AInvoked);
                    assertFalse(testState.beforeAction_BInvoked);
                    // BeforeAction_C is the only method that should have executed
                    assertTrue(testState.beforeAction_CInvoked);
                    assertFalse(testState.beforeAction_DInvoked);
                    assertFalse(testState.onAction1Invoked);
                    errorHandlerCalled.set(true);
                }
            });

            StateManagerTestUtils.doAction(stateManager, "Action1");
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }

        assertTrue("A FlowException should have been raised before reaching this point", errorHandlerCalled.get());
    }
    
    
    
}
