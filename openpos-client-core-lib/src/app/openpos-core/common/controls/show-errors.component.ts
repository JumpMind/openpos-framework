import {Component, Input } from '@angular/core';
import { AbstractControlDirective, AbstractControl } from '@angular/forms';

@Component({
    selector: 'app-show-errors',
    template: `
    <div *ngIf="shouldShowErrors()">
        <span *ngFor="let error of listOfErrors()">
        {{error}}<br/>
        </span>
    </div>
    `,
})
export class ShowErrorsComponent {
    private static readonly errorMessages = {
        'requireAtleastOne': () => 'At least one field is required',
        'pattern': () => 'Input did not match specified pattern',
        'required': () => 'This field is required',
        'minlength': () => 'Length is invalid',
        'maxlength': () => 'Length is invalid',
        'phoneUS' : () => 'Phone number is invalid',
        'phone' : () => 'Phone number is invalid',
        'date': () => 'Date is invalid'
    };

    @Input()
    private control: AbstractControlDirective | AbstractControl;

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
        (this.control.dirty || this.control.touched);
    }

    listOfErrors(): string[] {
        return Object.keys(this.control.errors)
        .map(field => this.getMessage(field, this.control.errors[field]));
    }

    private getMessage(type: string, params: any) {
        if( Object.keys(ShowErrorsComponent.errorMessages).includes(type)){
            return ShowErrorsComponent.errorMessages[type](params);
        } else {
            return "Invalid input";
        }
    }
}