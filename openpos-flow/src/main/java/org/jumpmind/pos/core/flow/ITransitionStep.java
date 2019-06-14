package org.jumpmind.pos.core.flow;


public interface ITransitionStep {
    
    public boolean isApplicable(Transition transition);
    public void arrive(Transition transition);
    
}
