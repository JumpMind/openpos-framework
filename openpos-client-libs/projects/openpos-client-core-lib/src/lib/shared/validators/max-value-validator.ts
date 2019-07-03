import { IValidator, IValidatorSpec } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class MaxValueValidator implements IValidator {
    name = this.constructor.name;

    constructor(public spec: MaxValueValidatorSpec) {
    }

    validationFunc: ValidatorFn = (ctrl: FormControl) => {
        let value = ctrl.value;
        if (value) {
            value = value.replace(',', '');
        }
        return Number(value) <= Number(this.spec.maximumValue) ? null : {
            // tslint:disable-next-line:object-literal-key-quotes
            'maxvalue': {
                valid: false
            }
        } as any;
    }
}

export interface MaxValueValidatorSpec extends IValidatorSpec {
    maximumValue: string;
}
