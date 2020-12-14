import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

/**
 * @ignore
 */
@Component({
  selector: 'app-auto-complete-address',
  templateUrl: './auto-complete-address.component.html',
  styleUrls: ['./auto-complete-address.component.scss']
})
export class AutoCompleteAddressComponent extends PosScreen<any> {


  buildScreen() { }

}
