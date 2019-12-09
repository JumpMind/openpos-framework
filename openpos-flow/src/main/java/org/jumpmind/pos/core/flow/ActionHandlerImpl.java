package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jumpmind.pos.core.flow.config.FlowUtil;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO should be called just ActionHandler, need to repackage annotation of the same name.
@Component
@org.springframework.context.annotation.Scope("prototype")
public class ActionHandlerImpl {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IBeforeActionService beforeActionService;

    @Autowired
    private ActionHandlerHelper helper;

    public boolean canHandleAction(Object state, Action action) {
        // if there is an action handler
        // AND it's not the current state firing this action.
        Method actionMethod = helper.getActionMethod(state, action);
        if (actionMethod != null && !isCalledFromState(state, action)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean handleAction(IStateManager stateManager, Object state, Action action) {
        // Get list of @BeforeAction methods in the state and execute them first
        beforeActionService.executeBeforeActionMethods(stateManager, state, action);

        Method actionMethod = helper.getActionMethod(state, action);
        if (actionMethod != null) {
            helper.invokeActionMethod(stateManager, state, action, actionMethod);
            return true;
        } else {
            return false;
        }
    }

    public boolean canHandleAnyAction(Object state, Action action) {
        return helper.getAnyActionMethod(state) != null && !isCalledFromState(state, action);
    }

    public boolean handleAnyAction(IStateManager stateManager, Object state, Action action) {
        // Get list of @BeforeAction methods in the state and execute them first
        beforeActionService.executeBeforeActionMethods(stateManager, state, action);
        
        Method anyActionMethod = helper.getAnyActionMethod(state);
        if (anyActionMethod != null) {
            helper.invokeActionMethod(stateManager, state, action, anyActionMethod);
            return true;
        } else {
            return false;
        }
    }


    protected boolean isCalledFromState(Object state, Action action) {
        StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();

        if (currentStack.length > 150) {
            helper.checkStackOverflow(StateManager.class, state, currentStack);
        }

        for (StackTraceElement stackFrame : currentStack) {
            Class<?> currentClass = helper.getClassFrom(stackFrame);
            if (currentClass != null && !Modifier.isAbstract(currentClass.getModifiers()) && FlowUtil.isState(currentClass)
                    && currentClass != state.getClass()) {
                return false;
            } else if (stackFrame.getClassName().equals(state.getClass().getName())) {
                return true;
            }

        }

        return false;
    }
    
 
    public IBeforeActionService getBeforeActionService() {
        return beforeActionService;
    }

    public void setBeforeActionService(IBeforeActionService beforeActionService) {
        this.beforeActionService = beforeActionService;
    }

    public ActionHandlerHelper getActionHandlerHelper() {
        return helper;
    }

    public void setActionHandlerHelper(ActionHandlerHelper actionHandlerHelper) {
        this.helper = actionHandlerHelper;
    }
}
