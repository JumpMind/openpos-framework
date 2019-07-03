import { Directive } from '@angular/core';
import { NG_VALIDATORS } from '@angular/forms';
import { PhoneUSValidatorFn } from '../validators/phone-us-validator';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[phoneUS]',
    providers: [{provide: NG_VALIDATORS, useExisting: PhoneUSValidatorFn, multi: true}]
})
export class PhoneUSValidatorDirective {}
