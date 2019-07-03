import {Component, Input } from '@angular/core';
import { AbstractControlDirective, AbstractControl } from '@angular/forms';

@Component({
    selector: 'app-show-errors',
    template: `
    <div *ngIf="shouldShowErrors()">
        <span>{{listOfErrors()[0]}}</span>
    </div>
    `,
})
export class ShowErrorsComponent {

    private static readonly errorMessages = {

        // @dynamic
        requireAtleastOne: () => 'At least one field is required',
        pattern: () => 'Input did not match specified pattern',
        required: () => 'This field is required',
        minlength: () => 'Length is invalid',
        maxlength: () => 'Length is invalid',
        phoneUS: () => 'Phone number is invalid',
        phone: () => 'Phone number is invalid',
        date: () => 'Date is invalid',
        datemmddyy: () => 'Date is invalid',
        dateddmmyy: () => 'Date is invalid',
        dateddmmyyyy: () => 'Date is invalid',
        noyeardate: () => 'Date is invalid',
        gt_0: () => 'Value must be greater than 0',
        minvalue: () => 'Value is less than minimum permitted',
        maxvalue: () => 'Value is greather than maximum permitted',
    };

    @Input()
    private control: AbstractControlDirective | AbstractControl;

    @Input()
    private additionalValidationMessages: Map<string, string>;

    /**
     * Provides a means to add or override errors provided by the ShowErrorsComponents.
     * @param errorName Name of the error to add or override
     * @param errorFn A function returning a string error message that will be displayed to the user.
     */
    public static registerError(errorName: string, errorFn: () => string): void {
        ShowErrorsComponent.errorMessages[errorName] = errorFn;
    }

    shouldShowErrors(): boolean {
        return this.control &&
        this.control.errors &&
        (this.control.dirty);
    }

    listOfErrors(): string[] {
        return Object.keys(this.control.errors)
        .map(field => this.getMessage(field, this.control.errors[field]));
    }

    private getMessage(type: string, params: any) {
        if ( this.additionalValidationMessages && Object.keys(this.additionalValidationMessages).includes(type)) {
            return this.additionalValidationMessages[type];
        } else if ( Object.keys(ShowErrorsComponent.errorMessages).includes(type)) {
            return ShowErrorsComponent.errorMessages[type](params);
        } else {
            return 'Invalid input';
        }
    }
}
