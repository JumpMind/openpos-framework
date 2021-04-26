import {Component, Injector, QueryList, ViewChild, ViewChildren} from '@angular/core';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import {LoyaltyCustomerFormInterface} from "./loyalty-customer-form.interface";
import {Observable} from "rxjs/internal/Observable";
import {MediaBreakpoints, OpenposMediaService} from "../../core/media/openpos-media.service";
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {FormBuilder} from "../../core/services/form-builder.service";
import {ShowErrorsComponent} from "../../shared/components/show-errors/show-errors.component";

@DialogComponent({
    name: 'LoyaltyCustomerDialog',
})
@Component({
    selector: 'app-loyalty-customer-form-dialog',
    templateUrl: './loyalty-customer-form-dialog.component.html',
    styleUrls: [ './loyalty-customer-form-dialog.component.scss']
})
export class LoyaltyCustomerFormDialogComponent extends PosScreen<LoyaltyCustomerFormInterface> {

    isMobile: Observable<boolean>;
    @ViewChild('formErrors') formErrors: ShowErrorsComponent;

    firstNameField : any;
    lastNameField : IFormElement;
    loyaltyNumberField : IFormElement;
    phoneField : IFormElement;
    phoneFields : IFormElement[] = [];
    phoneLabelFields : IFormElement[] = [];
    emailField : IFormElement;
    emailFields : IFormElement[] = [];
    emailLabelFields : IFormElement[] = [];

    handledFormFields : string[] = [];

    line1Field : IFormElement;
    line2Field : IFormElement;
    cityField : IFormElement;
    stateField : IFormElement;
    postalCodeField : IFormElement;
    countryField : IFormElement;
    addressIconLocationClass : string;

    constructor(injector: Injector, private media: OpenposMediaService, private formBuilder: FormBuilder) {
        super(injector);
        this.initIsMobile();
    }

    ngOnInit() {}

    initIsMobile(): void {
        this.isMobile = this.media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    getFormElementById(formElementId : string) : IFormElement {
        return this.screen.form.formElements.filter(element => element.id == formElementId)[0];
    }

    anyAddressFieldsPresent() : boolean {
        return !!(this.line1Field || this.line2Field || this.cityField || this.stateField || this.postalCodeField || this.countryField);
    }

    onFieldChanged(formElement: IFormElement) : void {
        if (formElement.valueChangedAction) {
            let form = this.formBuilder.buildFormPayload(this.screen.formGroup, this.screen.form);
            this.doAction(formElement.valueChangedAction, form);
        }
    }

    submitForm() : void {
        this.formBuilder.buildFormPayload(this.screen.formGroup, this.screen.form);
        this.doAction(this.screen.submitButton, this.screen.form);
    }

    buildScreen() : void {
        if(this.screen.isStructuredForm) {
            this.buildStructuredForm();
        }

        this.screen.formGroup = this.formBuilder.group(this.screen.form);
    }

    private buildStructuredForm(): void {
        this.handledFormFields = [
            'firstName',
            'lastName',
            'loyaltyNumber',
            'phone',
            'email',
            'line1',
            'line2',
            'city',
            'state',
            'postalCode',
            'country',
        ];

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

        if(this.line1Field) {
            this.addressIconLocationClass = 'icon1';
        } else if (this.line2Field) {
            this.addressIconLocationClass = 'icon2';
        } else if (this.cityField || this.stateField || this.postalCodeField) {
            this.addressIconLocationClass = 'icon3';
        } else if (this.countryField) {
            this.addressIconLocationClass = 'icon4';
        }

        this.phoneFields = [];
        this.phoneLabelFields = [];
        this.emailFields = [];
        this.emailLabelFields = [];
        if (this.screen && this.screen.form && this.screen.form.formElements) {
            this.screen.form.formElements.forEach(element => {
                if (element.id.match(/phonesList\d/)) {
                    this.phoneFields.push(element);
                    this.handledFormFields.push(element.id);
                }

                if (element.id.match(/phonesListLabel\d/)) {
                    this.phoneLabelFields.push(element);
                    this.handledFormFields.push(element.id);
                }

                if(element.id.match(/emailsList\d/)) {
                    this.emailFields.push(element);
                    this.handledFormFields.push(element.id);
                }

                if(element.id.match(/emailsListLabel\d/)) {
                    this.emailLabelFields.push(element);
                    this.handledFormFields.push(element.id);
                }
            });
        }
    }

}