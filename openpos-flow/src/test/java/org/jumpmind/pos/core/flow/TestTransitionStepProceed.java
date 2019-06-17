package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

public class TestTransitionStepProceed implements ITransitionStep {
    
    @In(scope = ScopeType.Device)
    protected IStateManager stateManager;    

    @Override
    public boolean isApplicable(Transition transition) {
        Assert.assertNotNull(stateManager);
        return StringUtils.equals(transition.getOriginalAction().getName(), "TestTransitionProceed");
    }

    @Override
    public void arrive(Transition transition) {
        Assert.assertNotNull(stateManager);
        transition.proceed();
    }

}
