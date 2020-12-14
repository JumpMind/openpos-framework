import { Component } from '@angular/core';
import { SelfCheckoutFormInterface } from './self-checkout-form.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';

@ScreenComponent({
  name: 'SelfCheckoutForm'
})
@Component({
  selector: 'app-self-checkout-form',
  templateUrl: './self-checkout-form.component.html',
  styleUrls: ['./self-checkout-form.component.scss']
})
export class SelfCheckoutFormComponent extends PosScreen<SelfCheckoutFormInterface> {


  buildScreen() { }
}
