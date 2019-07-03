import { IValidator } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class PhoneUSValidator implements IValidator {
    name = 'PhoneUS';

    validationFunc: ValidatorFn = PhoneUSValidatorFn;
}

export function PhoneUSValidatorFn(ctrl: FormControl) {
    const regex = /^[2-9][0-8][0-9][2-9][0-9]{6}$/;

    if (ctrl.value) {
        return regex.test(ctrl.value) ? null : {
            // tslint:disable-next-line:object-literal-key-quotes
            'phoneUS': {
                valid: false
            }
        } as any;
    } else {
        return null;
    }
}
