import { IValidator, IValidatorSpec } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class RegexValidator implements IValidator {
    name = this.constructor.name;

    constructor(public spec: RegexValidatorSpec) {
    }

    validationFunc: ValidatorFn = (ctrl: FormControl) => {

        if (ctrl.value) {
            const regex = new RegExp(this.spec.javascriptRegex, this.spec.flags ? this.spec.flags : '');
            return regex.test(ctrl.value) ? null : {
                // tslint:disable-next-line:object-literal-key-quotes
                'pattern': {
                    valid: false
                }
            } as any;
        } else {
            return null;
        }
    }
}

export interface RegexValidatorSpec extends IValidatorSpec {
    javascriptRegex: string;
    flags: string;
}
