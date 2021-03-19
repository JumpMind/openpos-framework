import {
    Component, Input
} from '@angular/core';
import { DynamicFormPartComponent } from "../../shared/screen-parts/dynamic-form-part/dynamic-form-part.component";
import {Observable} from "rxjs";
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {IActionItem} from "../../core/actions/action-item.interface";

@Component({
    selector: 'app-create-loyalty-customer-form-part',
    templateUrl: './create-loyalty-customer-form-part.component.html',
    styleUrls: ['./create-loyalty-customer-form-part.component.scss']
})
export class CreateLoyaltyCustomerFormPart extends DynamicFormPartComponent {

    @Input()
    addPhone: IActionItem;
    @Input()
    removePhone: IActionItem;

    isMobile: Observable<boolean>;
    firstNameField : IFormElement;
    lastNameField : IFormElement;
    emailField : IFormElement;
    loyaltyNumberField : IFormElement;
    postalCodeField : IFormElement;
    phoneFields : IFormElement[] = [];
    phoneLabelFields : IFormElement[] = [];

    ngOnInit() {
        super.ngOnInit();
        this.firstNameField = this.getFormElementById('firstName');
        this.lastNameField = this.getFormElementById('lastName');
        this.emailField = this.getFormElementById('email');
        this.loyaltyNumberField = this.getFormElementById('loyaltyNumber');
        this.postalCodeField = this.getFormElementById('postalCode');
    }

    getFormElementById(formElementId : string) {
        // Not a proper binding; this creates an infinite loop which is too taxing on the browser
        return this.screenData.formElements.filter(element => element.id == formElementId)[0];
    }

    screenDataUpdated(): void {
        this.updateData();
        this.phoneFields = [];
        this.phoneLabelFields = [];
        if (this.screenData && this.screenData.formElements) {
            this.screenData.formElements.forEach(element => {
                if (element.inputType === 'Phone') {
                    this.phoneFields.push(element);
                }

                if (element.id.indexOf('phonesLabel') != -1) {
                    this.phoneLabelFields.push(element);
                }
            });
        }
    }

}