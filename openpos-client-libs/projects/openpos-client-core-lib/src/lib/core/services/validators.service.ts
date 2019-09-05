import { ValidationConstants } from './../../shared/validators/validation.constants';
import { IValidator, IValidatorSpec } from './../interfaces/validator.interface';
import { Logger } from './logger.service';
import { Injectable } from '@angular/core';
import { ValidatorFn, Validators } from '@angular/forms';
// Since there are directives in shared that import validator service using the ../../shared
// barrel here causes a circular reference
import { LocaleService } from './locale.service';
import { OpenPosValidators } from '../../shared/validators/openpos-validators';

@Injectable({
    providedIn: 'root',
  })
export class ValidatorsService {

    private validators = new Map<string, Map<string, IValidator>>();

    constructor(private log: Logger, private localeService: LocaleService) {
        const USValidators = new Map<string, IValidator>();
        const NOLOCALEValidators = new Map<string, IValidator>();
        const CAValidators = new Map<string, IValidator>();

        USValidators.set('phone', OpenPosValidators.PHONE_US);
        CAValidators.set('phone', OpenPosValidators.PHONE_CA);
        CAValidators.set('postalcode', { name: 'PostalCode', validationFunc: Validators.minLength(6) });

        NOLOCALEValidators.set('giftcode', OpenPosValidators.GIFT_CODE);
        NOLOCALEValidators.set('date', OpenPosValidators.DATE_MMDDYYYY);
        NOLOCALEValidators.set('datemmddyy', OpenPosValidators.DATE_MMDDYY);
        NOLOCALEValidators.set('dateddmmyyyy', OpenPosValidators.DATE_DDMMYYYY);
        NOLOCALEValidators.set('dateddmmyy', OpenPosValidators.DATE_DDMMYY);

        NOLOCALEValidators.set('email', { name: 'Email', validationFunc: Validators.email } );
        NOLOCALEValidators.set('postalcode', { name: 'PostalCode', validationFunc: Validators.minLength(5) });
        NOLOCALEValidators.set('gt_0', OpenPosValidators.GT_0);
        NOLOCALEValidators.set('gte_0', OpenPosValidators.GTE_0);

        this.validators.set('en-us', USValidators);
        this.validators.set('us', USValidators);
        this.validators.set('ca', CAValidators);
        this.validators.set('en-ca', CAValidators);

        this.validators.set('NO-LOCALE', NOLOCALEValidators);
    }

    getValidator(validatorSpec: string | IValidatorSpec): ValidatorFn {
        const locale = this.localeService.getLocale();
        if (typeof (validatorSpec as IValidatorSpec) !== 'undefined' && (validatorSpec as IValidatorSpec).name) {
            // Support for dynamically loading validators specified by the server side.
            // If locale support is needed, that can be handled in the validator implementation
            const name = (validatorSpec as IValidatorSpec).name;
            const validatorInstance: IValidator =
                ValidationConstants.validators.find(entry => entry.name === name).validatorClass.prototype;
            validatorInstance.constructor.apply(validatorInstance, [validatorSpec]);
            return validatorInstance.validationFunc;
        } else {
            const name = (validatorSpec as string);

            if (name && locale) {
                const lname = name.toLowerCase();
                const llocale = locale.toLowerCase();
                // see if we have a validator map for the current locale
                //  and that locale has the validator we need
                if (this.validators.get(llocale) && this.validators.get(llocale).get(lname)) {
                    const v = this.validators.get(llocale).get(lname);
                    return typeof v !== 'undefined' ? v.validationFunc : undefined;
                }

                if (this.validators.get('NO-LOCALE') && this.validators.get('NO-LOCALE').get(lname)) {
                    const v = this.validators.get('NO-LOCALE').get(lname);
                    return typeof v !== 'undefined' ? v.validationFunc : undefined;
                }
            }
            this.log.info(`No validator found for locale '${locale}' validator name '${name}'. Using an 'always valid' validator`);
            return () => null;
        }
    }


}
