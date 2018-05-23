import { Component, OnInit, DoCheck, OnDestroy } from '@angular/core';
import { IScreen, AbstractTemplate } from '../..';
import { MultipleDynamicFormComponent } from '../../screens/multiple-dynamic-form/multiple-dynamic-form.component';


@Component({
    selector: 'app-multiple-dynamic-form-dialog',
    templateUrl: './multiple-dynamic-form-dialog.component.html'
})
export class MultipleDynamicFormDialogComponent extends MultipleDynamicFormComponent {
}
