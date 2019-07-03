import { IValidator } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class PhoneCAValidator implements IValidator {
    name = 'PhoneCA';

    validationFunc: ValidatorFn = (ctrl: FormControl) => {
        const regex = /^[0-9]{10}$/;

        if (ctrl.value) {
            return regex.test(ctrl.value) ? null : {
                // tslint:disable-next-line:object-literal-key-quotes
                'phone': {
                    valid: false
                }
            } as any;
        } else {
            return null;
        }
    }
}
