package org.jumpmind.pos.core.flow;


public interface ITransitionStep {
    
    boolean isApplicable(Transition transition);
    default void arrive(Transition transition){};
    default void afterTransition(TransitionContext transitionContext) {};
    
}
