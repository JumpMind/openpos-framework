package org.jumpmind.pos.core.ui;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * Adds timer functionality to an action which causes the action to be sent to
 * to the server from the client after {@code timeoutSecs}.  
 * If {@code displayCountdown} is {@code true} 
 * (the default) a timer is displayed that is updated every 
 * {@code countdownUpdateFrequencyMillis} milliseconds (default is every 1000ms) and
 * if {@code countdownPrefixText} and {@code countdownUnitsText} is provided 
 * the countdown value will be wrapped with parentheses 
 * (default display is ' (#s)', where '#' is the countdown number).
 * 
 * Note: currently only the client side GenericDialogComponent supports use
 * of ActionTimer, but other components can be adapted in the future to use it. 
 */

@Data
@Builder
public class ActionTimer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String countdownPrefixText = " (";
    private int timeoutSecs;
    @Builder.Default
    private boolean displayCountdown = true;
    @Builder.Default
    private int countdownUpdateFrequencyMillis = 1000;
    @Builder.Default
    private String countdownUnitsText = "s)";
    
}
