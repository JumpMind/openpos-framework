import { Component } from '@angular/core';
import { DynamicFormComponent } from './dynamic-form.component';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';

/**
 * @ignore
 */

@Component({
  selector: 'app-dynamic-form-dialog',
  templateUrl: './dynamic-form-dialog.component.html'
})
export class DynamicFormDialogComponent extends DynamicFormComponent {}
