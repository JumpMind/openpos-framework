import { Component } from '@angular/core';
import { SelfCheckoutTenderInterface } from './self-checkout-tender.interface';
import { IOptionItem } from '../../core/interfaces/option-item.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';

@ScreenComponent({
    name: 'SelfCheckoutTender'
})
@Component({
    selector: 'app-self-checkout-tender',
    templateUrl: './self-checkout-tender.component.html',
    styleUrls: ['./self-checkout-tender.component.scss']
})
export class SelfCheckoutTenderComponent extends PosScreen<SelfCheckoutTenderInterface> {

    buildScreen() {
    }

}
