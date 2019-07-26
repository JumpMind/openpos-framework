import { IValidator, IValidatorSpec } from '../../core/interfaces/validator.interface';
import { ValidatorFn, FormControl } from '@angular/forms';

export class RegexValidator implements IValidator {
    name = this.constructor.name;

    constructor(public spec: RegexValidatorSpec) {
    }

    validationFunc: ValidatorFn = (ctrl: FormControl) => {

        if (ctrl.value) {
            const regex = new RegExp(this.spec.javascriptRegex, this.spec.flags ? this.spec.flags : '');
            const result = regex.test(ctrl.value);
            console.log(`Regex is: ${regex}, control value is: ${ctrl.value}, result is: ${result}`);
            return result ? null : {
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
