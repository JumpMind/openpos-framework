import { Component, Input, Output, EventEmitter, ViewChildren, QueryList, ViewChild, AfterViewInit, ContentChild } from '@angular/core';
import { FormGroup, AbstractControl } from '@angular/forms';
import { ShowErrorsComponent } from '../show-errors/show-errors.component';
import { DynamicFormFieldComponent } from '../dynamic-form-field/dynamic-form-field.component';

/**
 * This is a component that wraps the form element so we can handle forms
 * in a consistent way througout the app.
 */
@Component({
    selector: 'app-form',
    templateUrl: './form.component.html'
})
export class FormComponent {

    @ViewChildren(DynamicFormFieldComponent) children: QueryList<DynamicFormFieldComponent>;
    @ContentChild('formErrors') formErrors: ShowErrorsComponent;

    @Input()
    form: FormGroup;

    /**
     * Submit event only emits if the form is valid
     */
    @Output()
    submitFormEvent = new EventEmitter();

    submitForm() {
        if (this.form.valid) {
            this.submitFormEvent.emit();
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
}
