package org.jumpmind.pos.core.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.jumpmind.pos.server.model.Action;

public class TestStates {

    public static class HomeState {

        @Out(scope = ScopeType.Conversation, required = false)
        private String optionalInjectionFullfiled = "optionalInjectionFullfiled";
        
        boolean departToSubflowCalled = false;
        boolean departStateCalled = false;
        boolean departGeneralCalled = false;
        boolean arriveCalled = false;
        Action departAction;

        public HomeState() {

        }

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @OnArrive
        public void arrivedAtHome(Action action) {
            arriveCalled = true;
        }
        
        @OnDepart
        public void depart(Action action) {
            departGeneralCalled = true;
        }          
        
        @OnDepart(toAnotherState=false)
        public void departToSubflow() {
            departToSubflowCalled = true;
        }
        
        @OnDepart(toSubflow=false)
        public void departState(Action action) {
            departStateCalled = true;
            departAction = action;
        }
        
        @ActionHandler
        protected void onReturn() {
            
        }
    }

    public static class HelpState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {

        }
    }

    public static class AboutState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {

        }
    }

    public static class SellState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {

        }

        @ActionHandler
        public void onCustomerLookupComplete(Action action) {

        }
    }

    public static class CustomerState implements IState {

        @In(scope = ScopeType.Config)
        private String customerFlowType;

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        @Out(scope = ScopeType.Flow)
        private String selectedCustomer;
        @Out(scope = ScopeType.Conversation)
        private String customerFlowTypeWorked;

        @Override
        public void arrive(Action action) {
            assertEquals("LOYALTY", this.customerFlowType);
            this.customerFlowTypeWorked = "customerFlowTypeWorked";
            this.selectedCustomer = "customer1234";
        }

        @ActionHandler
        public void onCustomerSignupComplete(Action action) {
            arrive(action.getCausedBy());
        }
    }

    public static class CustomerSearchState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {

        }
    }

    public static class CustomerSignupState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {

        }
    }

    public static abstract class AbstractRepostActionState implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @ActionHandler
        public void onAnyAction(Action action) {
            stateManager.doAction("RepostActionStateGotToSell");
        }
    }

    public static class RepostActionState extends AbstractRepostActionState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {
            assertNotNull(stateManager);
            stateManager.doAction(new Action("ShouldGoTo_AbstractRepostActionState.AnyAction"));
        }
    }

    public static class SubStateFlowScopePropogation1 implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        
        @Out(scope = ScopeType.Flow)
        private String flowScopeValue1;
        
        @Override
        public void arrive(Action action) {
            flowScopeValue1 = "flowScopeValue1";
            //stateManager.doAction("SubStateFlowScopePropogation2Action");
        }
        
        @ActionHandler
        public void onSubStateFlowScopePropogation2DONE(Action action) {
            
        }
    }
    
    public static class SubStateFlowScopePropogation2 implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        
        @In(scope = ScopeType.Flow)
        private String flowScopeValue1; // should be inherited.
        
        @Out(scope = ScopeType.Flow)
        private String flowScopeValue2;
        
        @Override
        public void arrive(Action action) {
            flowScopeValue2 = "flowScopeValue2";
            assertNotNull(flowScopeValue1);
            //stateManager.doAction("SubStateFlowScopePropogation2DONE");
        }
    }
    
    public static class SubStateReturnsWithTransitionState implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {
            if (action.getName().equals("ToSubState1")) {
                stateManager.doAction("FromSubStateToAnotherState");
            } else if (action.getName().equals("ToSubState2")) {
                stateManager.doAction("FromSubStateToAnotherSubState");
            }
        }
    }

    public static class InjectionFailedState implements IState {

        @In(scope = ScopeType.Device)
        private String failedInjection;

        @Override
        public void arrive(Action action) {

        }
    }

    public static class OptionalInjectionState implements IState {

        @In(scope = ScopeType.Conversation, required = false)
        private String optionalInjectionMissing;
        @In(scope = ScopeType.Conversation, required = false)
        private String optionalInjectionFullfiled;

        @Override
        public void arrive(Action action) {
            assertNull("optionalInjectionMissing", optionalInjectionMissing);
            assertNotNull("optionalInjectionFullfiled", optionalInjectionFullfiled);
        }
    }

    public static class ActionTestingState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Out(scope = ScopeType.Conversation)
        private boolean specificActionMethodCalled = false;
        @Out(scope = ScopeType.Conversation)
        private boolean anyActionMethodCalled = false;

        @Override
        public void arrive(Action action) {

        }

        @ActionHandler
        public void onSpecificAction(Action action) {
            specificActionMethodCalled = true;
        }

        @ActionHandler
        public void onAnyAction(Action action) {
            anyActionMethodCalled = true;
            if (action.getName().equals("Done")) {
                stateManager.doAction(action);
            }
        }
    }

    public static class TestScopesState implements IState {

        @Out(scope = ScopeType.Conversation)
        private String conversationScopeValue = "conversationScopeValue";
        @Out(scope = ScopeType.Session)
        private String sessionScopeValue = "sessionScopeValue";
        @Out(scope = ScopeType.Device)
        private String nodeScopeValue = "nodeScopeValue";

        @Override
        public void arrive(Action action) {

        }

    }

    public static class TransitionInterceptionState implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Out(scope = ScopeType.Conversation)
        private boolean onSellCalled = false;
        @Out(scope = ScopeType.Conversation)
        private boolean anyActionMethodCalled = false;

        @Override
        public void arrive(Action action) {

        }

        @ActionHandler
        public void onSell(Action action) {
            onSellCalled = true;
            stateManager.doAction(action);
        }

        @ActionHandler
        public void onAnyAction(Action action) {
            anyActionMethodCalled = true;
        }
    }

    public static abstract class AbstractStackOverflowState implements IState {

        @In(scope = ScopeType.Device)
        private IStateManager stateManager;

        @Override
        public void arrive(Action action) {
            stateManager.doAction(action);
        }

        @ActionHandler
        public void onAnyAction(Action action) {
            stateManager.doAction(action);
        }
    }

    public static class StackOverflowState extends AbstractStackOverflowState {

    }
    
    public static class MultiReturnActionTestState implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        @Override
        public void arrive(Action action) {

        }
    }    
    public static class MultiReturnActionInitialState implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        @Override
        public void arrive(Action action) {
            
        }
    }    
    public static class MultiReturnAction1State implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        @Override
        public void arrive(Action action) {
            
        }
    }    
    public static class MultiReturnAction2State implements IState {
        @In(scope = ScopeType.Device)
        private IStateManager stateManager;
        @Override
        public void arrive(Action action) {
            
        }
    }    
}
