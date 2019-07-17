import { Component } from '@angular/core';
import { SelfCheckoutOptionsPartInterface } from './self-checkout-options-part.interface';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { MessageProvider } from '../../../shared/providers/message.provider';



@ScreenPart({
    name: 'selfCheckoutOptionsPart'
})
@Component({
    selector: 'app-self-checkout-options-part',
    templateUrl: './self-checkout-options-part.component.html',
    styleUrls: ['./self-checkout-options-part.component.scss']
})
export class SelfCheckoutOptionsPartComponent extends ScreenPartComponent<SelfCheckoutOptionsPartInterface> {

    screenDataUpdated() { }

    buildScreen() { }

}
