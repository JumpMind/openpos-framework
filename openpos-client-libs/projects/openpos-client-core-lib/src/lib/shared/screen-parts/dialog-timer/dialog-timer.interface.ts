import { IActionItem } from '../../../core/actions/action-item.interface';
export interface DialogTimerInterface {
    /**
     * The action to send back to the server after timeoutSecs has elapsed.
     * Optional.
     */
    timeoutAction: string;
    /**
     * After this many seconds have elapsed, the dialog will be automatically closed
     * and the timeoutAction (if any) is sent back to the server. If <= 0, then
     * the timeout functionality will be disabled.
     */
    timeoutSecs: number;
    /**
     * If supplied, then the given text will be displayed in front of the countdown seconds.
     */
    timeoutMessage: string;
    /**
     * If true, then a countdown timer is displayed.
     */
    displayCountdown: boolean;

    /**
     * If > 0, then this is the frequency in milliseconds that the
     * countdown timer is updated with the current seconds remaining.
     */
    countdownUpdateFrequencyMillis: number;

    /**
     * If provided this text will be displayed after the countdown timer
     */
    countdownUnitsText: string;
}
