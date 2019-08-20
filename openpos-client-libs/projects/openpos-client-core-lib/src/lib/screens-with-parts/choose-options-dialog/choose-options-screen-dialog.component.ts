import { Component, Injector } from '@angular/core';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ChooseOptionsScreenDialogInterface } from './choose-options-screen-dialog.interface';

@DialogComponent({
    name: 'ChooseOptions'
})
@Component({
  selector: 'app-choose-options-screen-dialog',
  templateUrl: './choose-options-screen-dialog.component.html',
  styleUrls: ['./choose-options-screen-dialog.component.scss']
})
export class ChooseOptionsScreenDialogComponent extends PosScreen<ChooseOptionsScreenDialogInterface> {

  constructor(injector: Injector) {
      super(injector);
  }

  buildScreen() {
  }

}
