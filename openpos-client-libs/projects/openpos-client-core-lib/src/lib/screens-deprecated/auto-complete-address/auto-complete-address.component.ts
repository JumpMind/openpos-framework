import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

/**
 * TODO: Can probably move this to screens with parts
 */
@Component({
  selector: 'app-auto-complete-address',
  templateUrl: './auto-complete-address.component.html',
  styleUrls: ['./auto-complete-address.component.scss']
})
export class AutoCompleteAddressComponent extends PosScreen<any> {

  buildScreen() { }

}
