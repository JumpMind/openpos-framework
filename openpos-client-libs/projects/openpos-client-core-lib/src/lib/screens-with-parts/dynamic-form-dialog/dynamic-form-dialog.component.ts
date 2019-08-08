
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { DynamicFormDialogInterface } from './dynamic-form-dialog.interface';
import { PosScreen } from '../pos-screen/pos-screen.component';

@DialogComponent({
    name: 'DynamicForm',
})
@Component({
  selector: 'app-dynamic-form-dialog',
  templateUrl: './dynamic-form-dialog.component.html',
  styleUrls: [ './dynamic-form-dialog.component.scss']
})
export class DynamicFormDialogComponent extends PosScreen<DynamicFormDialogInterface> {

    buildScreen() {
    }

}
