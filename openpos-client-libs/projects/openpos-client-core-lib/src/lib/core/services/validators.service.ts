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

    private validators = new Map<string, Map<string, ValidatorFn>>();

    constructor(private log: Logger, private localeService: LocaleService) {
        const USValidators = new Map<string, ValidatorFn>();
        const NOLOCALEValidators = new Map<string, ValidatorFn>();
        const CAValidators = new Map<string, ValidatorFn>();

        USValidators.set('phone', OpenPosValidators.PhoneUS);
        CAValidators.set('phone', OpenPosValidators.PhoneCA);
        CAValidators.set('postalcode', Validators.minLength(6));

        NOLOCALEValidators.set('giftcode', OpenPosValidators.GiftCode);
        NOLOCALEValidators.set('date', OpenPosValidators.DateMMDDYYYY);
        NOLOCALEValidators.set('datemmddyy', OpenPosValidators.DateMMDDYY);
        NOLOCALEValidators.set('dateddmmyyyy', OpenPosValidators.DateDDMMYYYY);
        NOLOCALEValidators.set('dateddmmyy', OpenPosValidators.DateDDMMYY);

        NOLOCALEValidators.set('email', Validators.email);
        NOLOCALEValidators.set('postalcode', Validators.minLength(5));
        NOLOCALEValidators.set('gt_0', OpenPosValidators.GT_0);
        NOLOCALEValidators.set('gte_0', OpenPosValidators.GTE_0);

        this.validators.set('en-us', USValidators);
        this.validators.set('us', USValidators);
        this.validators.set('ca', CAValidators);
        this.validators.set('en-ca', CAValidators);

        this.validators.set('NO-LOCALE', NOLOCALEValidators);
    }

    getValidator(name: string): ValidatorFn {
        const locale = this.localeService.getLocale();
        if (name && locale) {

            const lname = name.toLowerCase();
            const llocale = locale.toLowerCase();
            // see if we have a validator map for the current locale
            //  and that locale has the validator we need
            if (this.validators.get(llocale) && this.validators.get(llocale).get(lname)) {
                return this.validators.get(llocale).get(lname);
            }

            if (this.validators.get('NO-LOCALE') && this.validators.get('NO-LOCALE').get(lname)) {
                return this.validators.get('NO-LOCALE').get(lname);
            }
        }
        this.log.info(`No validator found for locale '${locale}' validator name '${name}'. Using an 'always valid' validator`);
        return () => null;
    }


}
