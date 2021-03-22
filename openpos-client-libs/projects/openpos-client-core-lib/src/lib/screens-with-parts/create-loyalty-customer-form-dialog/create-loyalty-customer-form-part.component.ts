import {
    Component, Input
} from '@angular/core';
import { DynamicFormPartComponent } from "../../shared/screen-parts/dynamic-form-part/dynamic-form-part.component";
import {Observable} from "rxjs";
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {IActionItem} from "../../core/actions/action-item.interface";
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";

@Component({
    selector: 'app-create-loyalty-customer-form-part',
    templateUrl: './create-loyalty-customer-form-part.component.html',
    styleUrls: ['./create-loyalty-customer-form-part.component.scss']
})
export class CreateLoyaltyCustomerFormPart extends DynamicFormPartComponent {

    @Input()
    phoneIcon: string;
    @Input()
    addPhone: IActionItem;
    @Input()
    removePhone: IActionItem;

    @Input()
    emailIcon: string;
    @Input()
    addEmail: IActionItem;
    @Input()
    removeEmail: IActionItem;

    @Input()
    memberships: Membership[] = [];
    @Input()
    membershipsLabel: string;
    @Input()
    noMembershipsLabel: string;

    isMobile: Observable<boolean>;
    firstNameField : IFormElement;
    lastNameField : IFormElement;
    loyaltyNumberField : IFormElement;
    postalCodeField : IFormElement;
    phoneFields : IFormElement[] = [];
    phoneLabelFields : IFormElement[] = [];
    emailFields : IFormElement[] = [];
    emailLabelFields : IFormElement[] = [];

    ngOnInit() {
        super.ngOnInit();
        this.firstNameField = this.getFormElementById('firstName');
        this.lastNameField = this.getFormElementById('lastName');
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
        this.emailFields = [];
        this.emailLabelFields = [];
        if (this.screenData && this.screenData.formElements) {
            this.screenData.formElements.forEach(element => {
                if (element.id.match(/phones\d/)) {
                    this.phoneFields.push(element);
                }

                if (element.id.match(/phonesLabel\d/)) {
                    this.phoneLabelFields.push(element);
                }

                if(element.id.match(/emails\d/)) {
                    this.emailFields.push(element);
                }

                if(element.id.match(/emailsLabel\d/)) {
                    this.emailLabelFields.push(element);
                }
            });
        }
    }

}