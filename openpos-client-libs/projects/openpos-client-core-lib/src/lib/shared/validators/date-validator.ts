import { IValidator } from '../../core/interfaces/validator.interface';
import { FormControl, ValidatorFn } from '@angular/forms';
import { DateUtils } from '../utils/date.utils';

export class DateValidator implements IValidator {
    constructor(public name: string, public format: string) {
    }

    validationFunc: ValidatorFn = (ctrl: FormControl) => {
        if (ctrl.value && (typeof ctrl.value  === 'string')) {
            const dateParts = ctrl.value.split('/');
            if (dateParts.length !== 3) {
                return {
                    // tslint:disable-next-line:object-literal-key-quotes
                    'date': {
                        valid: false
                    }
                } as any;
            } else {
                // Currently assumes date is a 3 part date with month, day, year components
                const partPos = DateUtils.datePartPositions(this.format);
                const month = Number(dateParts[partPos.monthPos]);
                const dayOfMonth = Number(dateParts[partPos.dayOfMonthPos]);
                let year = Number(dateParts[partPos.yearPos]);
                year = DateUtils.normalizeDateYear(month, dayOfMonth, year);
                const date = new Date(year, month - 1, dayOfMonth);
                // `vs. parsed date '${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}'`);
                if (month === date.getMonth() + 1 && dayOfMonth === date.getDate() && year === date.getFullYear()) {
                    return null;
                } else {
                    return {
                        // tslint:disable-next-line:object-literal-key-quotes
                        'date': {
                            valid: false
                        }
                    } as any;
                }
            }
        }
    }
}
