import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { DynamicFormDialogInterface } from './dynamic-form-dialog.interface';

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