import { ValidatorFn, ValidationErrors, AbstractControl } from '@angular/forms';
import { DateValidator } from './date-validator';
import { PhoneCAValidator } from './phone-ca-validator';
import { PhoneUSValidator } from './phone-us-validator';
import { RequireAtLeastOneValidator } from './require-at-least-one-validator';
import { GiftCodeValidator } from './gift-code-validator';
import { GreaterThanEqZeroValidator } from './greater-than-eq-zero-validator';

export class OpenPosValidators {
    static readonly PHONE_CA = new PhoneCAValidator();
    static readonly PHONE_US = new PhoneUSValidator();
    static readonly GIFT_CODE = new GiftCodeValidator();
    static readonly REQUIRE_AT_LEAST_ONE = new RequireAtLeastOneValidator();
    static readonly DATE_MMDDYY: DateValidator = new DateValidator('DateMMDDYY', 'MMDDYY');
    static readonly DATE_MMDDYYYY = new DateValidator('DateMMDDYYYY', 'MMDDYYYY');
    static readonly DATE_DDMMYYYY = new DateValidator('DateDDMMYYYY', 'DDMMYYYY');
    static readonly DATE_DDMMYY = new DateValidator('DateDDMMYY', 'DDMMYY');

    static readonly GT_0 = new GreaterThanEqZeroValidator();
    static readonly GTE_0 = new GreaterThanEqZeroValidator();


    static LESS_THAN( limit: Number ): ValidatorFn {

        return (c: AbstractControl): ValidationErrors | null => {
            let value = c.value;
            if (value) {
                value = value.replace(',', '');
            }
            return Number(value) < limit ? null : {
                // tslint:disable-next-line:object-literal-key-quotes
                'less_than' : {
                    valid: false
                }
            };
        };
    }

    static LESS_THAN_OR_EQUAL( limit: Number ): ValidatorFn {

        return (c: AbstractControl): ValidationErrors | null => {
            let value = c.value;
            if (value) {
                value = value.replace(',', '');
            }
            return Number(value) <= limit ? null : {
                // tslint:disable-next-line:object-literal-key-quotes
                'less_than_equal' : {
                    valid: false
                }
            };
        };
    }


    static GREATER_THAN(limit: Number): ValidatorFn {
        return (c: AbstractControl): ValidationErrors | null => {
            let value = c.value;
            if (value) {
                value = value.replace(',', '');
            }
            return Number(value) > limit ? null : {
                // tslint:disable-next-line:object-literal-key-quotes
                'greater_than' : {
                    valid: false
                }
            };
        };
    }


    static GREATER_THAN_OR_EQUAL(limit: Number): ValidatorFn {
        return (c: AbstractControl): ValidationErrors | null => {
            let value = c.value;
            if (value) {
                value = value.replace(',', '');
            }
            return Number(value) >= limit ? null : {
                // tslint:disable-next-line:object-literal-key-quotes
                'greater_than_equal' : {
                    valid: false
                }
            };
        };
    }


}
