import { Directive, Input, HostListener } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[markDirtyOnSubmit]'
})
export class MarkDirtyOnSubmitDirective {

    @Input('markDirtyOnSubmit') formGroup: FormGroup;

    constructor() {

    }

    @HostListener('submit', ['$event'])
    onSubmit($event: Event): void {
        Object.getOwnPropertyNames(this.formGroup.controls).forEach( control => {
            this.formGroup.controls[control].markAsTouched();
            this.formGroup.controls[control].markAsDirty();
        });
    }
}
