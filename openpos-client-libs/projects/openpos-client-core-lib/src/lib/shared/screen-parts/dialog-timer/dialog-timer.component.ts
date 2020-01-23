import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Component, Injector, OnInit, OnDestroy } from '@angular/core';
import { ScreenPartComponent } from '../screen-part';
import { DialogTimerInterface } from './dialog-timer.interface';
import { DialogService } from '../../../core/services/dialog.service';

@ScreenPart({
    name: 'dialogTimer'})
@Component({
    selector: 'app-dialog-timer',
    templateUrl: './dialog-timer.component.html',
    styleUrls: ['./dialog-timer.component.scss']
})
export class DialogTimerComponent extends ScreenPartComponent<DialogTimerInterface> implements OnDestroy, OnInit {

    updateDisplayInterval: any;
    countdownInterval: any;
    secondsRemaining: number;
    displaySecondsRemaining: number;

    constructor(injector: Injector, private dialogService: DialogService) {
        super(injector);
    }

    ngOnInit(): void {
        super.ngOnInit();

    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
        if (!!this.updateDisplayInterval) {
            clearInterval(this.updateDisplayInterval);
        }
        if (!!this.countdownInterval) {
            clearInterval(this.countdownInterval);
        }

    }

    doTimeoutAction(): void {
        this.dialogService.closeDialog();
        if (!!this.screenData.timeoutAction) {
            this.actionService.doAction({action: this.screenData.timeoutAction});
        }
    }

    screenDataUpdated() {
        if (!this.countdownInterval && this.screenData.timeoutSecs > 0) {
            this.secondsRemaining = this.displaySecondsRemaining = this.screenData.timeoutSecs;

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

            if (this.screenData.countdownUpdateFrequencyMillis > 0) {
                this.updateDisplayInterval = setInterval(() => {
                    this.displaySecondsRemaining = this.secondsRemaining >= 0 ? this.secondsRemaining : 0;
                },
                this.screenData.countdownUpdateFrequencyMillis);
            }
        }
    }
}
