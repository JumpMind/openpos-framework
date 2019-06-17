import { Directive } from '@angular/core';
import { NG_VALIDATORS } from '@angular/forms';
import { OpenPosValidators } from '../validators/openpos-validators';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[requireAtleastOne]',
    providers: [{provide: NG_VALIDATORS, useExisting: OpenPosValidators.RequireAtleastOne, multi: true}]
})
export class RequireAtleastOneValidatorDirective {}
