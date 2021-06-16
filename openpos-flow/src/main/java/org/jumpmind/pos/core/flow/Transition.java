package org.jumpmind.pos.core.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Data;
import org.jumpmind.pos.core.flow.config.SubFlowConfig;
import org.jumpmind.pos.core.flow.config.TransitionStepConfig;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Transition {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Logger logGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");
    private final StateManagerLogger stateManagerLog = new StateManagerLogger(logGraphical);

    private List<? extends ITransitionStep> transitionSteps;
    private List<TransitionStepConfig> transitionStepConfigs;
    private StateContext sourceStateContext;
    private Object targetState;
    private StateManager stateManager;
    private Action originalAction;
    private TransitionResult.TransitionResultCode transitionResult;
    private Action queuedAction;
    private SubFlowConfig enterSubStateConfig;
    private StateContext resumeSuspendedState;
    private boolean autoTransition;

    private AtomicReference<ITransitionStep> currentTransitionStep = new AtomicReference<ITransitionStep>(null);
    private AtomicInteger stepIndex = new AtomicInteger(0);
    
    public Transition(List<TransitionStepConfig> transitionStepConfigs, StateContext sourceStateContext, Object targetState) {
        super();
        this.transitionStepConfigs = transitionStepConfigs;
        this.transitionSteps = createSteps(transitionStepConfigs);
        this.sourceStateContext = sourceStateContext;
        this.targetState = targetState;
    }

    public Transition(List<TransitionStepConfig> transitionStepConfigs, StateContext sourceStateContext, Object targetState, SubFlowConfig enterSubStateConfig, StateContext resumeSuspendedState, boolean autoTransition) {
            super();
            this.transitionStepConfigs = transitionStepConfigs;
            this.transitionSteps = createSteps(transitionStepConfigs);
            this.sourceStateContext = sourceStateContext;
            this.targetState = targetState;
            this.enterSubStateConfig = enterSubStateConfig;
            this.resumeSuspendedState = resumeSuspendedState;
            this.autoTransition = autoTransition;
    }

    public TransitionResult.TransitionResultCode getTransitionResult() {
        if (transitionResult != null) {
            return transitionResult;
        } else {
            return TransitionResult.TransitionResultCode.IN_PROGRESS;
        }
    }

    public void begin() {
        proceed();
    }
    
    public void proceed() {
        if (afterLastStep()) {
            if (transitionResult == null) {
                transitionResult = TransitionResult.TransitionResultCode.PROCEED;
            }
            stateManager.performOutjections(currentTransitionStep.get());
            return;
        } else if (afterFirstStep()) {
            stateManager.performOutjections(currentTransitionStep.get());
        }
        
        int localStepIndex = stepIndex.getAndIncrement();

        currentTransitionStep.set(transitionSteps.get(localStepIndex));
        
        executeCurrentStep();
    }

    protected boolean afterFirstStep() {
        return stepIndex.get() > 0;
    }

    protected boolean afterLastStep() {
        return stepIndex.get() >= transitionSteps.size();
    }
    
    protected boolean executeCurrentStep() {
        stateManager.performInjections(currentTransitionStep.get());
        
        boolean applicable = currentTransitionStep.get().isApplicable(this); 
        
        if (applicable) {
            stateManager.getStateManagerObservers().onTransition(stateManager.getApplicationState(), this, currentTransitionStep.get());
            currentTransitionStep.get().arrive(this); // This could come right recurse right back in on same thread or return after showing a screen.
        } else {
            if (log.isDebugEnabled()) {                
                log.debug("TransitionStep" + currentTransitionStep.get() + " was not applicable.");
            }
            proceed(); // recurse
        }
        
        return applicable;
    }

    public void cancelAndQueueAction(String action) {
        cancelAndQueueAction(new Action(action));
    }

    public void cancelAndQueueAction(Action action) {
        queuedAction = action;
        cancel();
    }
    
    public void cancel() {
        log.info("Transition was cancelled by " + currentTransitionStep.get());
        transitionResult = TransitionResult.TransitionResultCode.CANCEL;
        stepIndex.set(Integer.MAX_VALUE);
    }
    
    public boolean handleAction(Action action) {
        return stateManager.handleAction(currentTransitionStep.get(), action);
    }

    public IStateManager getStateManager() {
        return stateManager;
    }
    
    protected List<? extends ITransitionStep> createSteps(List<TransitionStepConfig> stepConfigs) {
        List<ITransitionStep> steps = new ArrayList<>(stepConfigs.size());
        for (TransitionStepConfig stepConfig : stepConfigs) {
            try {                
                ITransitionStep transitionStep = stepConfig.getTransitionStepClass().newInstance();
                steps.add(transitionStep);
            } catch (Exception ex) {
                throw new FlowException("Failed to create step " + stepConfig.getTransitionStepClass(), ex);
            }
        }
        
        return steps;
    }

    public ITransitionStep getCurrentTransitionStep() {
        return currentTransitionStep.get();
    }

    public List<? extends ITransitionStep> getTransitionSteps() {
        return transitionSteps;
    }

    public StateContext getSourceStateContext() {
        return sourceStateContext;
    }

    public Object getTargetState() {
        return targetState;
    }
    
    public Action getOriginalAction() {
        return originalAction;
    }

    public Action getQueuedAction() {
        return queuedAction;
    }

    public boolean isEnteringSubstate() {
        return getEnterSubStateConfig() != null;
    }

    public boolean isExitingSubstate() {
        return getResumeSuspendedState() != null;
    }
}
