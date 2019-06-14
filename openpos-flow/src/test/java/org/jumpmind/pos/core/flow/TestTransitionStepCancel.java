package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

public class TestTransitionStepCancel implements ITransitionStep {
    
    @In(scope = ScopeType.Device)
    protected IStateManager stateManager;    

    @Override
    public boolean isApplicable(Transition transition) {
        Assert.assertNotNull(stateManager);
        return StringUtils.equals(transition.getOriginalAction().getName(), "TestTransitionCancel");
    }

    @Override
    public void arrive(Transition transition) {
        Assert.assertNotNull(stateManager);
        transition.cancel();
    }

}
