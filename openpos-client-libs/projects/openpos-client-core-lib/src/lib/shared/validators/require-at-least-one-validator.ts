import { IValidator } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormGroup } from '@angular/forms';

export class RequireAtLeastOneValidator implements IValidator {
    name = 'RequireAtLeastOne';

    validationFunc: ValidatorFn = RequireAtLeastOneValidatorFn;
}

export function RequireAtLeastOneValidatorFn(group: FormGroup) {
    for (const name of Object.getOwnPropertyNames(group.value)) {
        const value = group.value[name];
        if (value !== '') {
            return null;
        }
    }
    return {
        // tslint:disable-next-line:object-literal-key-quotes
        'requireAtleastOne': {
            valid: false
        }
    } as any;
}
