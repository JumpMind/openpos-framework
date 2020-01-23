package org.jumpmind.pos.core.ui.messagepart;


import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * Adds timer functionality to a dialog which causes the dialog to display on the 
 * client for {@code timeoutSecs} and then will be automatically closed.  If a 
 * {@code timeoutAction} is supplied, then the given action will be sent upon 
 * automatic closing of the dialog.  If a {@code timeoutMessage} is provided, then the given message will be displayed in
 * front of the countdown seconds.  If {@code displayCountdown} is {@code true} 
 * (the default) a timer is displayed that is updated every 
 * {@code countdownUpdateFrequencyMillis} milliseconds (default is every 1000ms) and
 * if {@code countdownUnitsText} is provided (default value is 's' for seconds), 
 * the text given will be displayed after the countdown timer.  Spacing will be preserved
 * on the client for the {@code timeoutMessage} and {@code countdownUnitsText}.
 *
 */
@Data
@Builder
public class DialogTimerPart implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timeoutAction;
    private int timeoutSecs;
    private String timeoutMessage;
    @Builder.Default
    private boolean displayCountdown = true;
    @Builder.Default
    private int countdownUpdateFrequencyMillis = 1000;
    @Builder.Default
    private String countdownUnitsText = "s";
    
}
