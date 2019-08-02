import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { SelfCheckoutOptionsInterface } from './self-checkout-options.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';


@ScreenComponent({
    name: 'SelfCheckoutOptions'
})
@Component({
    selector: 'app-self-checkout-options',
    templateUrl: './self-checkout-options.component.html',
    styleUrls: ['./self-checkout-options.component.scss']
})
export class SelfCheckoutOptionsComponent extends PosScreen<SelfCheckoutOptionsInterface> {

    buildScreen() {
    }

}
