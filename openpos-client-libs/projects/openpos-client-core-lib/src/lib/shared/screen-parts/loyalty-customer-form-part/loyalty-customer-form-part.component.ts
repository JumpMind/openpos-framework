import {
    Component, Injector, Input, QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { DynamicFormPartComponent } from "../dynamic-form-part/dynamic-form-part.component";
import {Observable} from "rxjs";
import {IFormElement} from "../../../core/interfaces/form-field.interface";
import {IActionItem} from "../../../core/actions/action-item.interface";
import {Membership} from "../membership-display/memebership-display.interface";
import {ScreenPart} from "../../decorators/screen-part.decorator";
import {ScreenPartComponent} from "../screen-part";
import {IForm} from "../../../core/interfaces/form.interface";
import {DynamicFormFieldComponent} from "../../components/dynamic-form-field/dynamic-form-field.component";
import {ShowErrorsComponent} from "../../components/show-errors/show-errors.component";
import {FormGroup} from "@angular/forms";
import {FormBuilder} from "../../../core/services/form-builder.service";
import {LoyaltyCustomerFormInterface} from "../../../screens-with-parts/loyalty-customer-form-dialog/loyalty-customer-form.interface";



@ScreenPart({
    name: 'LoyaltyCustomerFormPart'
})
@Component({
    selector: 'app-loyalty-customer-form-part',
    templateUrl: './loyalty-customer-form-part.component.html',
    styleUrls: ['./loyalty-customer-form-part.component.scss']
})
export class LoyaltyCustomerFormPartComponent extends ScreenPartComponent<LoyaltyCustomerFormInterface> {

    @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;
    @ViewChild('formErrors') formErrors: ShowErrorsComponent;
    form: FormGroup;
    screenDataForm: FormGroup;

    firstNameField : IFormElement;
    lastNameField : IFormElement;
    loyaltyNumberField : IFormElement;
    phoneField : IFormElement;
    phoneFields : IFormElement[] = [];
    phoneLabelFields : IFormElement[] = [];
    emailField : IFormElement;
    emailFields : IFormElement[] = [];
    emailLabelFields : IFormElement[] = [];

    line1Field : IFormElement;
    line2Field : IFormElement;
    cityField : IFormElement;
    stateField : IFormElement;
    postalCodeField : IFormElement;
    countryField : IFormElement;

    @Input() set formName(name: string) {
        this.screenPartName = name;
    }

    @Input() isMobile: Observable<boolean>;
    @Input() submitButton: IActionItem;

    @Input() memberships: Membership[] = [];
    @Input() membershipsLabel: string;
    @Input() noMembershipsLabel: string;
    @Input() membershipsEnabled: boolean;

    constructor(private formBuilder: FormBuilder, injector: Injector) {
        super(injector);
    }

    ngOnInit() {
        super.ngOnInit();
        console.log('[ ======  LoyaltyCustomerFormPartComponent  ====== ]');
        console.dir(this.screenData);
        console.dir(this.screenData['form']);
        this.screenDataForm = this.screenData['form'];

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

    onFieldChanged(formElement: IFormElement) {
        if (formElement.valueChangedAction) {
            console.log('[onFieldChanged]');
            // console.dir(formElement);
            let form = this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.doAction( formElement.valueChangedAction, form);
        }
        // console.dir(this.form);
    }

    submitForm() {
        this.formBuilder.buildFormPayload(this.form, this.screenDataForm);
        this.doAction(this.submitButton, this.screenData);
    }

    anyAddressFieldsPresent() {
        return this.line1Field || this.line2Field || this.cityField || this.stateField || this.postalCodeField || this.countryField;
    }

    getFormElementById(formElementId : string) {
        return this.screenDataForm['formElements'].filter(element => element.id == formElementId)[0];
    }

    // This gets called automatically when screenData gets updated. This also runs before ngOnInit.
    screenDataUpdated(): void {
        console.log("[ SCREEN DATA UPDATED ]");
        console.dir(this.screenData);
        this.form = this.formBuilder.group(this.screenData);
        // console.dir(this.screenData);
        // console.dir(this.form);

        // this.updateData();
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