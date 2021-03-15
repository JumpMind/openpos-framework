import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { DynamicFormInterface } from '../dynamic-form-dialog/dynamic-form.interface';
import { PosScreen } from '../pos-screen/pos-screen.component';

@DialogComponent({
    name: 'CreateLoyaltyCustomerDialog',
})
@Component({
    selector: 'app-create-loyalty-customer-form-dialog',
    templateUrl: './create-loyalty-customer-form-dialog.component.html',
    styleUrls: [ './create-loyalty-customer-form-dialog.component.scss']
})
export class CreateLoyaltyFormDialogComponent extends PosScreen<DynamicFormInterface> {

    buildScreen() {
    }

}