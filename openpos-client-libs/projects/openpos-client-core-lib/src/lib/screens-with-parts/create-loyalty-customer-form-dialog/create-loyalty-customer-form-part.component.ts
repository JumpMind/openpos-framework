import {
    Component
} from '@angular/core';
import { DynamicFormPartComponent } from "../../shared/screen-parts/dynamic-form-part/dynamic-form-part.component";
import {Observable} from "rxjs";
import {IFormElement} from "../../core/interfaces/form-field.interface";

@Component({
    selector: 'app-create-loyalty-customer-form-part',
    templateUrl: './create-loyalty-customer-form-part.component.html',
    styleUrls: ['./create-loyalty-customer-form-part.component.scss']
})
export class CreateLoyaltyCustomerFormPart extends DynamicFormPartComponent {

    isMobile: Observable<boolean>;

    firstNameField : IFormElement;
    lastNameField : IFormElement;
    emailField : IFormElement;
    phoneField : IFormElement;
    loyaltyNumberField : IFormElement;
    postalCodeField : IFormElement;

    ngOnInit() {
        super.ngOnInit();

        this.firstNameField = this.getFormElementById('firstName');
        this.lastNameField = this.getFormElementById('lastName');
        this.emailField = this.getFormElementById('email');
        this.phoneField = this.getFormElementById('phone');
        this.loyaltyNumberField = this.getFormElementById('loyaltyNumber');
        this.postalCodeField = this.getFormElementById('postalCode');
    }

    getFormElementById(formElementId : string) {
        // Not a proper binding; this creates an infinite loop which is too taxing on the browser
        return this.screenData.formElements.filter(element => element.id == formElementId)[0];
    }

}