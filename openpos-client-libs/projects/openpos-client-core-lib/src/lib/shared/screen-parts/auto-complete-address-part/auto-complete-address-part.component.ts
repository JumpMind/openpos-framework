import { ViewChildren, AfterViewInit, Input, QueryList, ViewChild, Component } from '@angular/core';
import { FormGroup, AbstractControl } from '@angular/forms';
import { ScreenPartComponent } from '../screen-part';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { MessageProvider } from '../../providers/message.provider';
import { FormBuilder } from '../../../core/services/form-builder.service';
import { DynamicFormFieldComponent } from '../../components/dynamic-form-field/dynamic-form-field.component';
import { ShowErrorsComponent } from '../../components/show-errors/show-errors.component';
import { IForm } from '../../../core/interfaces/form.interface';
import { IFormElement } from '../../../core/interfaces/form-field.interface';
import { IActionItem } from '../../../core/interfaces/action-item.interface';



@ScreenPart({
    name: 'AutoCompleteAddressPart'
})
@Component({
    selector: 'app-auto-complete-address-part',
    templateUrl: './auto-complete-address-part.component.html',
    styleUrls: ['./auto-complete-address-part.component.scss']
})
export class AutoCompleteAddressPartComponent extends ScreenPartComponent<IForm> implements AfterViewInit {

    @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;
    @ViewChild('formErrors') formErrors: ShowErrorsComponent;
    form: FormGroup;

    streetAddress: IFormElement;
    addressLine2: IFormElement;
    locality: IFormElement;
    state: IFormElement;
    postalCode: IFormElement;
    country: IFormElement;

    nonAddressFields: IFormElement[];

    buttons: IFormElement[];

    private _alternateSubmitActions: string[];

    @Input() set formName(name: string) {
        this.screenPartName = name;
    }

    @Input() submitButton: IActionItem;

    constructor(private formBuilder: FormBuilder, messageProvider: MessageProvider) {
        super(messageProvider);
    }

    setAddress(address: any) {
        if (address.streetNumber) {
            this.form.get('streetAddress').setValue(address.streetNumber + ' ' + address.streetName);
        } else {
            this.form.get('streetAddress').setValue(address.streetName);
        }
        this.form.get('locality').setValue(address.locality);
        this.form.get('state').setValue(address.state);
        this.form.get('postalCode').setValue(address.postalCode);
        this.form.get('country').setValue(address.country);
        this.onFieldChanged(this.country);
    }

    screenDataUpdated() {
        this.buttons = new Array<IFormElement>();
        this.nonAddressFields = new Array<IFormElement>();

        this.form = this.formBuilder.group(this.screenData);

        if (this.screenData && this.screenData.formElements) {
            this.screenData.formElements.forEach(element => {
                if (element.elementType === 'Button') {
                    this.buttons.push(element);
                }

                const isAddressField = this.parseAddressField(element);
                if (!isAddressField) {
                    this.nonAddressFields.push(element);
                }
            });
        }
    }

    private parseAddressField(element: IFormElement): boolean {
        let isAddressField = false;
        if (element.id === 'streetAddress') {
            this.streetAddress = element;
        } else if (element.id === 'addressLine2') {
            this.addressLine2 = element;
            isAddressField = true;
        } else if (element.id === 'locality') {
            this.locality = element;
            isAddressField = true;
        } else if (element.id === 'state') {
            this.state = element;
            isAddressField = true;
        } else if (element.id === 'postalCode') {
            this.postalCode = element;
            isAddressField = true;
        } else if (element.id === 'country') {
            this.country = element;
            isAddressField = true;
        }
        return isAddressField;
    }

    @Input()
    get alternateSubmitActions(): string[] {
        return this._alternateSubmitActions;
    }

    set alternateSubmitActions(actions: string[]) {
        this._alternateSubmitActions = actions;
        if (actions) {
            actions.forEach(action => {

                this.sessionService.registerActionPayload(action, () => {
                    if (this.form.valid) {
                        this.formBuilder.buildFormPayload(this.form, this.screenData);
                        return this.screenData;
                    } else {
                        // Show errors for each of the fields where necessary
                        Object.keys(this.form.controls).forEach(f => {
                            const control = this.form.get(f);
                            control.markAsTouched({ onlySelf: true });
                        });
                        throw Error('form is invalid');
                    }
                });
            });
        }
    }


    ngAfterViewInit() {
        // Delays less than 1 sec do not work correctly.
        this.display(1000);
    }

    public display(delay: number) {
        const nonReadonlyChildren = this.children.filter(child => {
            return child.isReadOnly() === false;
        });

        if (nonReadonlyChildren.length > 0) {
            setTimeout(() => nonReadonlyChildren[0].focus(), delay);
        }
    }

    submitForm() {
        if (this.form.valid) {
            this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.sessionService.onAction(this.submitButton, this.screenData);
        } else {
            // Set focus on the first invalid field found
            const invalidFieldKey = Object.keys(this.form.controls).find(key => {
                const ctrl: AbstractControl = this.form.get(key);
                return ctrl.invalid && ctrl.dirty;
            });
            if (invalidFieldKey) {
                const invalidField = this.children.find(f => f.controlName === invalidFieldKey).field;
                if (invalidField) {
                    const invalidElement = document.getElementById(invalidFieldKey);
                    if (invalidElement) {
                        invalidElement.scrollIntoView();
                    } else {
                        invalidField.focus();
                    }
                }
            } else {
                if (this.formErrors.shouldShowErrors()) {
                    const formErrorList = this.formErrors.listOfErrors();
                    if (formErrorList && formErrorList.length > 0) {
                        document.getElementById('formErrorsWrapper').scrollIntoView();
                    }
                }
            }
        }
    }

    onFieldChanged(formElement: IFormElement) {
        if (formElement.valueChangedAction) {
            this.formBuilder.buildFormPayload(this.form, this.screenData);
            this.sessionService.onAction(formElement.valueChangedAction, this.screenData);
        }
    }

    onButtonClick(formElement: IFormElement) {
        this.sessionService.onAction(formElement.action, null, formElement.confirmationDialog);
    }
}
