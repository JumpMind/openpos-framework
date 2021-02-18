import { Component } from '@angular/core';
import { TenderPartInterface } from './tender-part.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {takeUntil} from 'rxjs/operators';
import { ITender } from './tender.interface';

@ScreenPart({
    name: 'TenderPart'
})
@Component({
    selector: 'app-tender-part',
    templateUrl: './tender-part.component.html',
    styleUrls: ['./tender-part.component.scss']
})
export class TenderPartComponent extends ScreenPartComponent<TenderPartInterface> {
    alternateSubmitActions: IActionItem[] = [];
    alternateSubmitActionNames: string[] = [];
    amountCss: string = '';
    isRoundUpAvailable: boolean = false;

    screenDataUpdated() {
        if (this.screenData.amountDue && parseFloat(this.screenData.amountDue.amount) < 0) {
            this.amountCss = 'negative';
        }
        else {
            this.amountCss = '';
        }
        if (this.screenData.roundUpAvailable)
        {
            this.isRoundUpAvailable = true;
        }
        else {
            this.isRoundUpAvailable = false;
        }
        // Register form data with possible actions
        if (this.screenData.optionsList) {
            if (this.screenData.optionsList.options) {
                this.alternateSubmitActions.push(...this.screenData.optionsList.options);
            }
            if (this.screenData.optionsList.additionalButtons) {
                this.alternateSubmitActions.push(...this.screenData.optionsList.additionalButtons);
            }
            if (this.screenData.optionsList.linkButtons) {
                this.alternateSubmitActions.push(...this.screenData.optionsList.linkButtons);
            }

            this.alternateSubmitActionNames = this.alternateSubmitActions.map(actionItem => actionItem.action);

            this.keyPressProvider.globalSubscribe(this.alternateSubmitActions).pipe(
                takeUntil(this.destroyed$)
            ).subscribe(action => this.doAction(action));
        }
    }

    voidTender(tender: ITender, index: number) {
        this.doAction(tender.voidButton, index);
    }

    roundUp()
    {
        if (this.isRoundUpAvailable && this.screenData.roundUpButton)
        {
            this.doAction(this.screenData.roundUpButton.action);
        }
    }
}
