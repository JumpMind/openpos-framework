package org.jumpmind.pos.core.flow;


import lombok.Data;

@Data
public class TransitionResult {

    public static enum TransitionResultCode {
        PROCEED,
        CANCEL
    }

    private TransitionResultCode transitionResultCode;
    private Transition transition;

}
