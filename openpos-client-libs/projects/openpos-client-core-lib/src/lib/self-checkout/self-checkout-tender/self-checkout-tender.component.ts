import { Component } from '@angular/core';
import { SelfCheckoutTenderInterface } from './self-checkout-tender.interface';
import { IOptionItem } from '../../screens-deprecated/choose-options/option-item.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';

@ScreenComponent({
    name: 'SelfCheckoutTender'
})
@Component({
    selector: 'app-self-checkout-tender',
    templateUrl: './self-checkout-tender.component.html',
    styleUrls: ['./self-checkout-tender.component.scss']
})
export class SelfCheckoutTenderComponent extends PosScreen<SelfCheckoutTenderInterface> {

    public currentView: string;
    public selectedOption: IOptionItem;
    public optionItems: IOptionItem[];
    public amountTitle = 'Amount Due';
    public amountDue = '0.00';

    constructor() {
        super();
    }

    buildScreen() {
        if (this.screen.amountDue) {
            this.amountTitle = this.screen.amountDue.name;
            this.amountDue = this.screen.amountDue.amount;
        }
    }
}
