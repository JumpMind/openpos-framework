import { Component } from '@angular/core';
import { PosScreen } from '../../screens-with-parts/pos-screen.component';

/**
 * @ignore
 */
@Component({
  selector: 'app-full-page-image',
  templateUrl: './full-page-image.component.html'
})
export class FullPageImageComponent extends PosScreen<any> {

    buildScreen() {}

}
