import { IValidator } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class GreaterThanEqZeroValidator implements IValidator {
    name = 'GTE_0';

    validationFunc: ValidatorFn = (ctrl: FormControl) => {
        let value = ctrl.value;
        if (value) {
            value = value.replace(',', '');
        }
        return Number(value) >= 0 ? null : {
            // tslint:disable-next-line:object-literal-key-quotes
            'gt_0': {
                valid: false
            }
        } as any;
    }
}
