import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';

/**
 * @ignore
 */
@ScreenComponent({
  name: 'EmbeddedWebPage'
})
@DialogComponent({
  name: 'EmbeddedWebPage'
})
@Component({
  selector: 'app-embedded-web-page',
  templateUrl: './embedded-web-page.component.html'
})
export class EmbeddedWebPageComponent extends PosScreen<any> {

  buildScreen() {}
}
