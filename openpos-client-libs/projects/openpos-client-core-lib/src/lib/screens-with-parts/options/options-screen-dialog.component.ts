import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { OptionsInterface } from './options.interface';

@DialogComponent({
    name: 'Options'
})
@Component({
  selector: 'app-options-screen-dialog',
  templateUrl: './options-screen-dialog.component.html',
  styleUrls: ['./options-screen-dialog.component.scss']
})
export class OptionsScreenDialogComponent extends PosScreen<OptionsInterface> {

  buildScreen() {
  }

}
