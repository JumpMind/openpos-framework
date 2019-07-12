import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'DynamicList'
})
@Component({
  selector: 'app-dynamic-list',
  templateUrl: './dynamic-list.component.html'
})
export class DynamicListComponent extends PosScreen<any> {

  buildScreen() {}

}
