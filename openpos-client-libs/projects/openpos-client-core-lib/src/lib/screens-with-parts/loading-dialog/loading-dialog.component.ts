import { LoadingDialogInterface } from './loading-dialog.interface';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';

@DialogComponent({
    name: 'LoadingDialog',
})
@Component({
  selector: 'app-loading-dialog',
  templateUrl: './loading-dialog.component.html',
  styleUrls: [ './loading-dialog.component.scss']
})
export class LoadingDialogComponent extends PosScreen<LoadingDialogInterface> {

    buildScreen() {
    }

}
