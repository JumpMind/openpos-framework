import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

/**
 * @ignore
 */
@Component({
  selector: 'app-print-preview',
  templateUrl: './print-preview.component.html'
})
export class PrintPreviewComponent extends PosScreen<any> {

    buildScreen() {}
}
