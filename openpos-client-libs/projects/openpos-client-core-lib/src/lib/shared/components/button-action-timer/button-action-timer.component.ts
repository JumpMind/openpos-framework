import { Input, Component, OnInit, OnDestroy } from '@angular/core';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';
import { Observable, of } from 'rxjs';

@Component({
    selector: 'app-button-action-timer',
    templateUrl: './button-action-timer.component.html',
    styleUrls: ['./button-action-timer.component.scss']

})
export class ButtonActionTimerComponent implements OnInit, OnDestroy {
    /**
     * The action to send back to the server when the timer is triggered.
     */
    @Input() action: IActionItem;
    /**
     * An observable used to listen for a notification to indicate that the
     * counter should be reset and started over.
     */
    @Input() reset: Observable<boolean> = of(false);

    updateDisplayInterval: any;
    countdownInterval: any;
    secondsRemaining: number;
    displaySecondsRemaining: number;

    constructor(protected actionService: ActionService) {
    }


    ngOnInit(): void {
        this.initTimers();
        if (!!this.reset) {
            this.reset.subscribe(val => {
                if (!!val) {
                    console.log('Resetting button timer');
                    this.initTimers();
                }
            });
        }
    }

    protected initTimers(): void {
        if (!! this.countdownInterval) {
            clearInterval(this.countdownInterval);
            this.countdownInterval = null;
        }
        if (!! this.updateDisplayInterval) {
            clearInterval(this.updateDisplayInterval);
            this.updateDisplayInterval = null;
        }

        if (!this.countdownInterval && !! this.action.actionTimer && this.action.actionTimer.timeoutSecs > 0) {
            this.secondsRemaining = this.displaySecondsRemaining = this.action.actionTimer.timeoutSecs;

            this.countdownInterval = setInterval(() => {
                this.secondsRemaining--;
                if (this.secondsRemaining <= 0) {
                    clearInterval(this.countdownInterval);
                    if (!! this.updateDisplayInterval) {
                        clearInterval(this.updateDisplayInterval);
                    }
                    this.countdownInterval = null;
                    this.updateDisplayInterval = null;
                    this.doTimeoutAction();
                }
            }, 1000);

            if (this.action.actionTimer.countdownUpdateFrequencyMillis > 0) {
                this.updateDisplayInterval = setInterval(() => {
                    this.displaySecondsRemaining = this.secondsRemaining >= 0 ? this.secondsRemaining : 0;
                },
                this.action.actionTimer.countdownUpdateFrequencyMillis);
            }
        }

    }

    doTimeoutAction(): void {
        console.log(`Button action timeout (${this.action.actionTimer.timeoutSecs}s) triggered, sending action '${this.action.action}'`);
        this.actionService.doAction(this.action);
    }

    ngOnDestroy(): void {
        if (!!this.updateDisplayInterval) {
            clearInterval(this.updateDisplayInterval);
        }
        if (!!this.countdownInterval) {
            clearInterval(this.countdownInterval);
        }
    }

}
