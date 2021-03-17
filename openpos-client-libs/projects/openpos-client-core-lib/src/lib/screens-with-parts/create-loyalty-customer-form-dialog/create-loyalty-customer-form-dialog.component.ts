import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { Component } from '@angular/core';
import { PosScreen } from '../pos-screen/pos-screen.component';
import {CreateLoyaltyCustomerFormInterface} from "./create-loyalty-customer-form.interface";

@DialogComponent({
    name: 'CreateLoyaltyCustomerDialog',
})
@Component({
    selector: 'app-create-loyalty-customer-form-dialog',
    templateUrl: './create-loyalty-customer-form-dialog.component.html',
    styleUrls: [ './create-loyalty-customer-form-dialog.component.scss']
})
export class CreateLoyaltyFormDialogComponent extends PosScreen<CreateLoyaltyCustomerFormInterface> {

    buildScreen() {
        console.log("[[ ========== CreateLoyaltyFormDialogComponent ========== ]]");
        console.dir(this);
    }

}