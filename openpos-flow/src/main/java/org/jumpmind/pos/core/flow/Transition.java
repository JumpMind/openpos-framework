package org.jumpmind.pos.core.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transition {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Logger logGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");
    private final StateManagerLogger stateManagerLog = new StateManagerLogger(logGraphical);    
    
    private CountDownLatch latch;

    private List<? extends ITransitionStep> transitionSteps;
    private StateContext sourceStateContext;
    private Object targetState;
    private StateManager stateManager;
    private Action originalAction;
    private TransitionResult transitionResult;
    
    private AtomicReference<ITransitionStep> currentTransitionStep = new AtomicReference<ITransitionStep>(null);
    private AtomicInteger stepIndex = new AtomicInteger(0);
    
    public Transition(List<? extends ITransitionStep> transitionSteps, StateContext sourceStateContext, Object targetState) {
        super();
        this.transitionSteps = cloneSteps(transitionSteps);
        this.sourceStateContext = sourceStateContext;
        this.targetState = targetState;
        
        latch = new CountDownLatch(transitionSteps.size());
    }

    public TransitionResult execute(StateManager stateManager, Action originalAction) {
        this.stateManager = stateManager;
        this.originalAction = originalAction;
        proceed();
        
        if (transitionResult == null) {
            throw new FlowException("Invalid: transitionResult must be non-null at this point.");
        }
        
        return transitionResult;
    }
    
    public void proceed() {
        if (afterLastStep()) {
            if (transitionResult == null) {
                transitionResult = TransitionResult.PROCEED;
            }
            stateManager.performOutjections(currentTransitionStep.get());
            latch.countDown();
            return;
        } else if (afterFirstStep()) {
            stateManager.performOutjections(currentTransitionStep.get());
            latch.countDown();
        }
        
        int localStepIndex = stepIndex.getAndIncrement();

        currentTransitionStep.set(transitionSteps.get(localStepIndex));
        
        executeCurrentStep();
        
        waitForEverybody();
    }

    private void waitForEverybody() {
        try {
            stateManager.setTransactionRestFlag(true);
            latch.await();
        } catch (InterruptedException ex) {
            throw new FlowException("Transition await interupted.", ex);
        } finally {
            stateManager.setTransactionRestFlag(false);
        }
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
            stateManagerLog.logTranistionStep(this, currentTransitionStep.get());
            currentTransitionStep.get().arrive(this); // This could come right recurse right back in on same thread or return after showing a screen.
        } else {
            if (log.isDebugEnabled()) {                
                log.debug("TransitionStep" + currentTransitionStep.get() + " was not applicable.");
            }
            proceed(); // recurse
        }
        
        return applicable;
    }
    
    public void cancel() {
        log.info("Transition was canncelled by " + currentTransitionStep.get());
        transitionResult = TransitionResult.CANCEL;
        stepIndex.set(Integer.MAX_VALUE);
        while (latch.getCount() > 0) {            
            latch.countDown();
        }
    }
    
    public boolean handleAction(Action action) {
        return stateManager.handleAction(currentTransitionStep.get(), action);
    }
    
    protected List<? extends ITransitionStep> cloneSteps(List<? extends ITransitionStep> steps) {
        List<ITransitionStep> clonedSteps = new ArrayList<>(steps.size());
        for (ITransitionStep step : steps) {
            try {                
                ITransitionStep clonedStep = step.getClass().newInstance();
                clonedSteps.add(clonedStep);
            } catch (Exception ex) {
                throw new FlowException("Failed to clone step " + step, ex);
            }
        }
        
        return clonedSteps;
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

}
