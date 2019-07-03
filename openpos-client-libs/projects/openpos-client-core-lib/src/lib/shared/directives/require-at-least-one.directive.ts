import { Directive } from '@angular/core';
import { NG_VALIDATORS } from '@angular/forms';
import { RequireAtLeastOneValidatorFn } from '../validators/require-at-least-one-validator';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[requireAtleastOne]',
    providers: [{provide: NG_VALIDATORS, useExisting: RequireAtLeastOneValidatorFn, multi: true}]
})
export class RequireAtleastOneValidatorDirective {}
