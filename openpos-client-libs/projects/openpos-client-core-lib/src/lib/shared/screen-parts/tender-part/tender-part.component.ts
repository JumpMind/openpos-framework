import { Component } from '@angular/core';
import { TenderPartInterface } from './tender-part.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
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

    alternateSubmitActions: string[] = [];

    screenDataUpdated() {
        // Register form data with possible actions
        if (this.screenData.optionsList) {
            if (this.screenData.optionsList.options) {
                this.screenData.optionsList.options.forEach(value => {
                    this.alternateSubmitActions.push(value.action);
                });
            }
            if (this.screenData.optionsList.additionalButtons) {
                this.screenData.optionsList.additionalButtons.forEach(value => {
                    this.alternateSubmitActions.push(value.action);
                });
            }
            if (this.screenData.optionsList.linkButtons) {
                this.screenData.optionsList.linkButtons.forEach(value => {
                    this.alternateSubmitActions.push(value.action);
                });
            }
        }
    }

    voidTender(tender: ITender, index: number) {
        this.doAction(tender.voidButton, index);
    }

}
