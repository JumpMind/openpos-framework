import {
    Component, Input
} from '@angular/core';
import { DynamicFormPartComponent } from "../../shared/screen-parts/dynamic-form-part/dynamic-form-part.component";
import {Observable} from "rxjs";
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {IActionItem} from "../../core/actions/action-item.interface";
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";

@Component({
    selector: 'app-loyalty-customer-form-part',
    templateUrl: './loyalty-customer-form-part.component.html',
    styleUrls: ['./loyalty-customer-form-part.component.scss']
})
export class LoyaltyCustomerFormPart extends DynamicFormPartComponent {

    @Input()
    phoneIcon: string;
    @Input()
    addPhone: IActionItem;
    @Input()
    removePhone: IActionItem;
    @Input()
    clearPhone: IActionItem;

    @Input()
    emailIcon: string;
    @Input()
    addEmail: IActionItem;
    @Input()
    removeEmail: IActionItem;
    @Input()
    clearEmail: IActionItem;

    @Input()
    countrySelected: IActionItem;
    @Input()
    stateSelected: IActionItem;

    @Input()
    profileIcon: string;
    @Input()
    locationIcon: string;
    @Input()
    loyaltyNumberIcon: string;

    @Input()
    membershipsIcon: string;
    @Input()
    membershipsEnabled: boolean;
    @Input()
    memberships: Membership[] = [];
    @Input()
    membershipsLabel: string;
    @Input()
    noMembershipsLabel: string;

    @Input()
    isMobile: Observable<boolean>;

    firstNameField : IFormElement;
    lastNameField : IFormElement;
    loyaltyNumberField : IFormElement;
    phoneField : IFormElement;                  // CTX_FORM_FIELD: PHONE
    phoneFields : IFormElement[] = [];          // CTX_FORM_FIELD: PHONE_LIST
    phoneLabelFields : IFormElement[] = [];
    emailField : IFormElement;                  // CTX_FORM_FIELD: EMAIL
    emailFields : IFormElement[] = [];          // CTX_FORM_FIELD: EMAIL_LIST
    emailLabelFields : IFormElement[] = [];

    line1Field : IFormElement;
    line2Field : IFormElement;
    cityField : IFormElement;
    stateField : IFormElement;
    postalCodeField : IFormElement;
    countryField : IFormElement;

    ngOnInit() {
        super.ngOnInit();
        this.firstNameField = this.getFormElementById('firstName');
        this.lastNameField = this.getFormElementById('lastName');
        this.loyaltyNumberField = this.getFormElementById('loyaltyNumber');
        this.phoneField = this.getFormElementById('phone');
        this.emailField = this.getFormElementById('email');

        this.line1Field = this.getFormElementById('line1');
        this.line2Field = this.getFormElementById('line2');
        this.cityField = this.getFormElementById('city');
        this.stateField = this.getFormElementById('state');
        this.postalCodeField = this.getFormElementById('postalCode');
        this.countryField = this.getFormElementById('country');
    }

    getFormElementById(formElementId : string) {
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