import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'Options'
})
@Component({
  selector: 'app-options',
  templateUrl: './options.component.html',
  styleUrls: ['./options.component.scss']
})
export class OptionsComponent extends PosScreen<any> {

  buildScreen() {}

}
