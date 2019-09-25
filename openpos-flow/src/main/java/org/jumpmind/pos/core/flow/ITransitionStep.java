package org.jumpmind.pos.core.flow;


public interface ITransitionStep {
    
    boolean isApplicable(Transition transition);
    void arrive(Transition transition);
    void afterTransition(TransitionContext transitionContext);
    
}
