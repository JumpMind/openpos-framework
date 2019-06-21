import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidatorFn } from '@angular/forms';
import { IFormElement } from '../interfaces/form-field.interface';
import { IForm } from '../interfaces/form.interface';

// cannot import the ../../shared barrel here because of a circular reference
import { ValidatorsService } from './validators.service';
import { OpenPosValidators } from '../../shared/validators/openpos-validators';

@Injectable({
    providedIn: 'root'
})
export class FormBuilder {

    constructor(private validatorService: ValidatorsService) { }

    group(form: IForm, extraValidators: ValidatorFn[] = []): FormGroup {
        const group: any = {};
        if (form.formElements) {
            form.formElements.forEach((element) => {
                group[element.id] = new FormControl(element.value, this.createControlValidators(element));

                // For a DATE type element, there is also a hidden field to handle picking of dates using
                // a date picker, need to add a FormControl for that hidden input also.
                if (element.inputType && element.inputType !== 'DatePartChooser' && element.inputType.toLowerCase().indexOf('date') >= 0) {
                    group[element.id + 'Hidden'] = new FormControl();
                }
            });
        }

        return new FormGroup(group, this.createFormLevelValidators(form, extraValidators));
    }

    buildFormPayload(formGroup: FormGroup, form: IForm): IForm {
        if (form.formElements) {
            form.formElements.forEach(element => {
                // FormGroup.value object will only contain the form controls that are ENABLED
                // See docs for FormGroup.

                if (element.hasOwnProperty('checked')) {
                    // Disabled checkboxes don't have a value in formGroup, so check for that
                    // and use the control value instead if necessary.  Should probably work
                    // this way for all controls or we should only be sending back controls that are
                    // enabled.  TBD
                    if (formGroup.value[element.id]) {
                        element.checked = (formGroup.value[element.id] === true || formGroup.value[element.id] === 'checked');
                    } else if (formGroup.controls[element.id]) {
                        element.checked = formGroup.controls[element.id].value === true || formGroup.controls[element.id].value === 'checked';
                    }
                } else if (element.elementType === 'Input' && element.inputType !== 'Radio') {
                    element.value = formGroup.value[element.id];
                } else if (element.elementType === 'Input' && element.inputType === 'Radio') {
                    element.value = element.selectedIndex.toString();
                }
            });
        }

        return form;
    }

    /**
 * Since an individual validator cannot be added after construction, this method
 * provides a way to add extra validators onto those already normally assigned to the
 * IFormElement.
 * A list of validators is returned which include the provided list of extraValidators.
 * The returned list of validators can then be set on the form. See the setValidators method
 * on the FormComponent class.
 *
 * @param extraValidators Optional additional validators to be added to the form.
 */
    private createControlValidators(element: IFormElement, extraValidators: ValidatorFn[] = []): ValidatorFn[] {
        let validators: ValidatorFn[] = [];
        if (element.required) {
            validators.push(Validators.required);
        }

        if (element.pattern) {
            validators.push(Validators.pattern(element.pattern));
        }

        if (element.minLength) {
            validators.push(Validators.minLength(element.minLength));
        }

        if (element.maxLength) {
            validators.push(Validators.maxLength(element.maxLength));
        }

        if (element.minValue) {
            validators.push(Validators.min(element.minValue));
        }

        if (element.maxValue) {
            validators.push(Validators.max(element.maxValue));
        }

        validators.push(this.validatorService.getValidator(element.inputType));
        if (element.validators) {
            element.validators.forEach(v => validators.push(this.validatorService.getValidator(v)));
        }

        validators = validators.concat(extraValidators);
        if (!!element.additionalValidators) {
            validators = validators.concat(element.additionalValidators);
        }
        return validators;
    }


    /**
     * Since an individual validator cannot be added after construction, this method
     * provides a way to add extra validators onto those already provided by the form.
     * A list of validators is returned which include the provided list of extraValidators.
     * The returned list of validators can then be set on the form. See the setValidators method
     * on the FormGroup class.
     *
     * @param extraValidators Optional additional validators to be added to the form.
     */
    private createFormLevelValidators(form: IForm, extraValidators: ValidatorFn[] = []): ValidatorFn[] {
        let validators: ValidatorFn[] = [];
        if (form.requiresAtLeastOneValue) {
            validators.push(OpenPosValidators.RequireAtleastOne);
        }

        validators = validators.concat(extraValidators);
        return validators;
    }

}
