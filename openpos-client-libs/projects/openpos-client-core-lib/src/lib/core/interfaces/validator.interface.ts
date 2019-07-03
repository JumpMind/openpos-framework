import { ValidatorFn, FormControl } from '@angular/forms';

/**
 * Allows for Validator implementations that can be dynamically specified by
 * a validator name provided by the server. Implementors need to provide the name
 * of the validator and an angular ValidatorFn implementation.
 */
export interface IValidator {
    name: string;
    validationFunc: ValidatorFn;
}

/**
 * Each implementation of an IValidator that originates from the server
 * should provide a corresponding IValidatorSpec implementation which defines
 * the name of the matching IValidator implementation and any additional
 * properties that should be given to the implemented IValidator instance.
 */
export interface IValidatorSpec {
    name: string;
}
