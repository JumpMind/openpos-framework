import { IValidator } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class GiftCodeValidator implements IValidator {
    name = 'GiftCode';

    validationFunc: ValidatorFn = (ctrl: FormControl) => {
        return ctrl.value && ctrl.value.length > 2 ? null : {
            // tslint:disable-next-line:object-literal-key-quotes
            'minlength': {
                valid: false
            }
        } as any;
    }
}
