import { Component, Injector } from '@angular/core';
import { ScanInputDialogInterface } from './scan-input-dialog.interface';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';

@DialogComponent({
  name: 'ScanInputDialog'
})
@Component({
  selector: 'app-scan-input-dialog',
  templateUrl: './scan-input-dialog.component.html',
  styleUrls: ['./scan-input-dialog.component.scss']
})
export class ScanInputDialogComponent extends PosScreen<ScanInputDialogInterface> {

  buildScreen() { }
}
