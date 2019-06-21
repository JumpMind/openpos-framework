import { Directive } from '@angular/core';
import { NG_VALIDATORS } from '@angular/forms';
import { OpenPosValidators } from '../validators/openpos-validators';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[phoneUS]',
    providers: [{provide: NG_VALIDATORS, useExisting: OpenPosValidators.PhoneUS, multi: true}]
})
export class PhoneUSValidatorDirective {}
