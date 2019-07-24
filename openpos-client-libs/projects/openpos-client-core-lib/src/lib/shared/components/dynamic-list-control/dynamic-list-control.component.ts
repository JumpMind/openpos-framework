import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { IListComponent } from '../../../core/interfaces/list-component.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { IFormElement } from '../../../core/interfaces/form-field.interface';
import { IForm } from '../../../core/interfaces/form.interface';
import { SessionService } from '../../../core/services/session.service';
import { ScreenService } from '../../../core/services/screen.service';
import { FormBuilder } from '../../../core/services/form-builder.service';
import { ActionService } from '../../../core/actions/action.service';

@Component({
    selector: 'app-dynamic-list-control',
    templateUrl: './dynamic-list-control.component.html',
    styleUrls: ['./dynamic-list-control.component.scss']
})

export class DynamicListControlComponent implements OnInit {

    @Input() formGroup: FormGroup;
    @Input() controlName: string;
    @Input() list: IListComponent;  // @ViewChild('list')
    @Input() submitButton: IActionItem;
    @Input() keyboardLayout: string;

    @Output() valueChange = new EventEmitter<any>();

    form: FormGroup;
    buttons: IFormElement[];

    private _initialized = false;
    private _hasAddValueField = false;
    private _hasSummaryField = false;
    private _isText = false;
    private _isNumeric = false;
    private _isMoney = false;
    private _total = 0;

    private _alternateSubmitActions: string[];
    private _screenForm: IForm;

    constructor(
        public screenService: ScreenService,
        private formBuilder: FormBuilder,
        public actionService: ActionService ) {
        this.log('Constructing DynamicListControlComponent...');
        this.log('initialized = ' + this._initialized);
    }

    @Input()
    get screenForm(): IForm {
        return this._screenForm;
    }

    set screenForm(screenForm: IForm) {
        this._screenForm = screenForm;
        this.buttons = new Array<IFormElement>();

        this.form = this.formBuilder.group(screenForm);

        screenForm.formElements.forEach(element => {
            if (element.elementType === 'Button') {
                this.buttons.push(element);
            }
        });
    }

    @Input()
    get alternateSubmitActions(): string[] {
        return this._alternateSubmitActions;
    }

    set alternateSubmitActions(actions: string[]) {
        this._alternateSubmitActions = actions;
        if (actions) {
            actions.forEach(action => {
                this.actionService.registerActionPayload(action, () => {
                    if (this.form.valid) {
                        this.formBuilder.buildFormPayload(this.form, this._screenForm);
                        return this._screenForm;

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

    ngOnInit(): void {
        this.log('Initializing DynamicListControlComponent...')
        if (!this.list.valueList) {
            this.list.valueList = [];
            this.log('Value List was undefined, initialized to empty');
        }

        this.list.formErrors = [];
        this.log('Initialized form errors to empty');

        this._hasAddValueField = (this.list.addingAllowed && (this.list.addValueField != null));
        this._hasSummaryField = (this.list.summaryField != null);
        this.log('Has Add Value Field: ' + this._hasAddValueField + ', Has Summary Field: ' + this._hasSummaryField);

        this.log('List Component Name: ' + this.list.name);
        this.log('List has ' + this.list.valueList.length + ' item(s)');
        this.log('Add Value label: ' + this.list.addValueField.label + ', value: ' + this.list.addValueField.value + ', icon: ' + this.list.addValueField.iconName);
        this.log('Summary label: ' + this.list.summaryField.label + ', value: ' + this.list.summaryField.value + ', icon: ' + this.list.summaryField.iconName);
        this.log('Requires at least one value: ' + this.list.requiresAtLeastOneValue);

        if (!this.form) {
            this.log('Form Group is not populated');
            if (this.formGroup) {
                this.form = this.formGroup;
                this.log('Able to populate Form Group from @Input');
            }
        }

        this._isMoney = (this.list.valueType ? this.list.valueType === 'Money' : false);
        this.log('Managing a ' + (this._isMoney ? '' : 'non-') + 'money list');
        this._isNumeric = (this.list.valueType ? this._isMoney || this.list.valueType === 'Numeric' : false);
        this.log('Managing a ' + (this._isNumeric ? '' : 'non-') + 'numeric list');
        this._isText = (!this._isNumeric && !this._isMoney);
        this.log('Managing a ' + (this._isText ? '' : 'non-') + 'text list');

        this._initialized = true;
        this.log('initialized = ' + this._initialized);
    }

    public getValue(index: number): string {
        if ((index < 0) || (index >= this.list.valueList.length)) {
            return null;
        } else {
            return this.list.valueList[index];
        }
    }

    public addValue(value: string, index: number = -1): void {
        this.log('Adding value ' + value + ' at position ' + index + ', current field value ' + this.list.addValueField.value);
        if (this.list.addingAllowed && this.list.addValueField && !this.isEmptyValue(value)) {
            this.log('Adding is allowed and enabled and a value has been entered');
            if (index < 0) {
                this.list.valueList.push(this.list.addValueField.value);
            } else {
                this.list.valueList.splice(index, 0, this.list.addValueField.value);
            }

            this.updateSummaryValue(this.list.addValueField.value, true);

            this.log('Setting addValueField back to default value');
            this.initializeAddValueField();
            this.list.formErrors = [];
        }
    }

    public removeValue(index: number): void {
        this.log('Removing value at position ' + index);
        if (this.list.removingAllowed) {
            this.updateSummaryValue(this.getValue(index), false);

            if (index < 0) {
                this.list.valueList.pop();
            } else {
                this.list.valueList.splice(index, 1);
            }

            this.list.formErrors = [];
        }
    }

    public displayValue(value: string): string {
        if (this._isMoney) {
            const amount = parseFloat(this.toCleanValue(value));
            return '$' + amount.toFixed(2);

        } else if (this._isNumeric) {
            return value;
        }

        //  If we made it here, this is a text type with no special formatting.

        return value;
    }

    public onAddValueFieldChange(value: string) {
        if (this.hasAddValueField()) {
            this.list.addValueField.value = value;
        }
    }

    public updateSummaryValue(value: string, increment: boolean = true) {
        if (this._hasSummaryField) {
            if (this._isNumeric) {
                const amount = parseFloat(this.toCleanValue(value));
                this._total += (increment ? amount : -amount);
                this.log('Total is now ' + this._total);
                this.list.summaryField.value = this.displayValue(this._total.toString());
            }
        }
    }

    public initializeAddValueField(): void {
        if (this._hasAddValueField) {
            this.list.addValueField.value = this.displayValue(this._isNumeric ? '0' : '');
        }
    }

    public isMoneyType(): boolean {
        return this._isMoney;
    }

    public isNumericType(): boolean {
        return this._isNumeric;
    }

    public isTextType(): boolean {
        return this._isText;
    }

    public getSummaryValue(): string {
        return (this._hasSummaryField ? this.list.summaryField.value : null);
    }

    public setSummaryValue(value: string) {
        if (this._hasSummaryField) {
            this.list.summaryField.value = this.displayValue(value);
        }
    }

    private toCleanValue(value: string) {
        if (value) {
            const pos = value.indexOf('$');
            return (pos < 0 ? value : value.substring(pos + 1));
        }
        return (this._isNumeric ? '0' : '');
    }

    private isEmptyValue(value: string) {
        if (this._isText) {
            return (value == null || value.length === 0);
        } else if (this._isNumeric) {
            return (parseFloat(this.toCleanValue(value)) === 0);
        }
        return false;
    }

    public hasAddValueField(): boolean {
        //    this.log('hasAddValueField() returning ' + this._hasAddValueField);
        return this._hasAddValueField;
    }

    public getAddValueFieldLabel(): string {
        if (this.hasAddValueField()) {
            this.log('getAddValueFieldLabel() returning ' + (this.list.addValueField.label ? this.list.addValueField.label : 'NULL'));
            return this.list.addValueField.label;
        }
    }

    public getAddValueFieldValue(): string {
        if (this.hasAddValueField()) {
            this.log('getAddValueFieldValue() returning ' + (this.list.addValueField.value ? this.list.addValueField.value : 'BLANK'));
            return (this.list.addValueField.value ? this.list.addValueField.value : '');
        }
    }

    public hasSummaryField(): boolean {
        //    this.log('hasSummaryField() returning ' + this._hasSummaryField);
        return this._hasSummaryField;
    }

    public getSummaryField(): IFormElement {
        //    this.log('getSummaryField() returning ' + (this.list.summaryField ? 'object' : 'NULL'));
        return (this.hasSummaryField() ? this.list.summaryField : null);
    }

    public hasValueListItems(): boolean {
        return (this.list.valueList && (this.list.valueList.length > 0));
    }

    public getValueList(): Array<string> {
        return this.list.valueList;
    }

    public onSummaryFieldChanged(formElement: IFormElement): void {
        this.valueChange.emit(formElement);
    }

    public onButtonClick(button: IActionItem) {
        if (button.action) {
            if (button.action === this.submitButton.action) {
                this.onSubmit(this.submitButton.action);

            } else if (this.alternateSubmitActions) {
                //  Is this an alternate submit action?

                for (const action in this.alternateSubmitActions) {
                    if (action === button.action) {
                        this.onSubmit(action);
                    }
                }
            }
        }
    }

    public onSubmit(action: string = null) {
        this.log('onSubmit() called with action ' + (action ? action : '*undefined*'));
        this.list.formErrors = [];
        let isValid = true;

        //  If we didn't get an action, use the submit button's action or
        //  a default if we don't have one.

        if (!action) {
            action = (this.submitButton && this.submitButton.action ? this.submitButton.action : 'Next');
        }
        this.log('Submit action is ' + action);

        this.log('Validating list...');

        //  If the list says it must have at least one value, it is an error if we
        //  have no items.

        if (this.list.requiresAtLeastOneValue && (this.list.valueList.length < 1)) {
            this.list.formErrors.push('The ' + this.list.valueListHeader + ' list must contain at least one value');
        }

        //  Walk the list of values and make sure they are all valid.

        for (let i = 0; i < this.list.valueList.length; i++) {
            //  All of the items in the list must have a value.

            if (this.isEmptyValue(this.list.valueList[i])) {
                this.list.formErrors.push('Item #' + (i + 1) + ' has no value');
            }

            //  Perform type-specific validations.

            if (this._isNumeric) {
                const value = parseFloat(this.toCleanValue(this.list.valueList[i]));
                if (!isNaN(this.list.addValueField.minValue) && value < this.list.addValueField.minValue) {
                    this.list.formErrors.push('Item #' + (i + 1) + ' is less than the minimum value ' + this.list.addValueField.minValue);

                } else if (!isNaN(this.list.addValueField.maxValue) && value > this.list.addValueField.maxValue) {
                    this.list.formErrors.push('Item #' + (i + 1) + ' is greater than the maximum value ' + this.list.addValueField.maxValue);
                }

            } else if (this._isText) {
                if (this.list.valueList[i].length < this.list.addValueField.minLength) {
                    this.list.formErrors.push('Item #' + (i + 1) + ' is shorter than the minimum length ' + this.list.addValueField.minLength);

                } else if (this.list.valueList[i].length > this.list.addValueField.maxLength) {
                    this.list.formErrors.push('Item #' + (i + 1) + ' is longer than the maximum length ' + this.list.addValueField.maxLength);
                }

                if (this.list.valueType === 'NumericText') {
                    const dummy = parseInt(this.list.valueList[i], 10);
                    if (isNaN(dummy)) {
                        this.list.formErrors.push('Item #' + (i + 1) + ' is not numeric')
                    }
                }
            }
        }

        //  See if we had any errors.

        if (this.list.formErrors && this.list.formErrors.length > 0) {
            //  There were errors.

            this.log('Form has ' + this.list.formErrors.length + ' error(s)');
            isValid = false;
            // document.getElementById('formErrorsWrapper').scrollIntoView();

        } else {
            //  No errors, perform the submit action.

            this.log('Form has no errors, firing action ' + action + '...');
            this.actionService.doAction({action}, this.list.valueList);
        }

        this.log('onSubmit() returning ' + isValid);
        return isValid;
    }

    public log(message: string) {
        // if (message)  {
        //     console.log(message);
        // }
    }
}
