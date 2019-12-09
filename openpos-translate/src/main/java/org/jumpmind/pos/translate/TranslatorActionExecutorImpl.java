package org.jumpmind.pos.translate;

import java.lang.reflect.Modifier;

import org.jumpmind.pos.core.flow.ActionHandlerHelper;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslatorActionExecutorImpl implements ITranslatorActionExecutor {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int CHECK_STACK_OVERFLOW_THRESHOLD = 150;
    
    @Autowired
    ActionHandlerHelper helper;
    
    @Override
    public boolean canExecute(Object translator, Action action) {
        return this.canExecuteAction(translator, action) || this.canExecuteAnyAction(translator, action);
    }

    @Override
    public void executeAction(Object translator, IStateManager stateManager, Action action, Object...args) {
        if (this.canExecuteAction(translator, action)) {
            helper.invokeActionMethod(stateManager, translator, action, helper.getActionMethod(translator, action), args);
        } else if (this.canExecuteAnyAction(translator, action)) {
            helper.invokeActionMethod(stateManager, translator, action, helper.getAnyActionMethod(translator), args);
        } else {
            logger.debug("No handling available for action '{}' in class {}", action.getName(), translator.getClass().getName());
        }
    }

    
    public boolean canExecuteAction(Object obj, Action action) {
        // if there is an action handler
        // AND it's not the current state firing this action.
        return helper.getActionMethod(obj, action) != null && ! isCalledFromSelf(obj, action);
    }
    
    public boolean canExecuteAnyAction(Object obj, Action action) {
        return helper.getAnyActionMethod(obj) != null && ! isCalledFromSelf(obj, action);
    }

    protected boolean isCalledFromSelf(Object obj, Action action) {
        StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();

        if (currentStack.length > CHECK_STACK_OVERFLOW_THRESHOLD) {
            helper.checkStackOverflow(this.getClass(), obj, currentStack);
        }

        for (StackTraceElement stackFrame : currentStack) {
            Class<?> currentClass = helper.getClassFrom(stackFrame);
            if (currentClass != null && ! Modifier.isAbstract(currentClass.getModifiers())
                    && currentClass != obj.getClass()) {
                return false;
            } else if (stackFrame.getClassName().equals(obj.getClass().getName())) {
                return true;
            }

        }

        return false;
    }
    
}
