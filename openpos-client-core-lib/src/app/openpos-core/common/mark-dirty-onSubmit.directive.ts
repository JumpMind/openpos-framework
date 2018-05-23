import { Directive, Input, HostListener } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { formDirectiveProvider } from '@angular/forms/src/directives/ng_form';

@Directive({
    selector: '[markDirtyOnSubmit]'
})
export class MarkDirtyOnSubmit {

    @Input("markDirtyOnSubmit") formGroup: FormGroup;

    constructor() {

    }

    @HostListener('submit', ['$event'])
    onSubmit($event: Event): void{
        Object.getOwnPropertyNames(this.formGroup.controls).forEach( control => {
            this.formGroup.controls[control].markAsTouched();
            this.formGroup.controls[control].markAsDirty();
        });
    }
}
