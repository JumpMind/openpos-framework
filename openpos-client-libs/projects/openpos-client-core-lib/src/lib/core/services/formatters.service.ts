import { Logger } from './logger.service';
import { Injectable } from '@angular/core';
import { LocaleService } from './locale.service';
import { IFormatter } from '../../shared/formatters/formatter.interface';
import { PhoneUSFormatter } from '../../shared/formatters/phone-us.formatter';
import { PhoneCAFormatter } from '../../shared/formatters/phone-ca.formatter';
import { DateTimeCAFormatter } from '../../shared/formatters/datetime-ca.formatter';
import { PostalCodeCAFormatter } from '../../shared/formatters/postal-code-ca.formatter';
import { NumericFormatter } from '../../shared/formatters/numeric.formatter';
import { GiftCodeFormatter } from '../../shared/formatters/gift-code.formatter';
import { MoneyFormatter } from '../../shared/formatters/money.formatter';
import { PercentageFormatter } from '../../shared/formatters/percentage.formatter';
import { PostalCodeFormatter } from '../../shared/formatters/postal-code.formatter';
import { IncomeFormatter } from '../../shared/formatters/income.formatter';
import { StateIDNumberFormatter } from '../../shared/formatters/state-id-number.formatter';
import { DecimalFormatter } from '../../shared/formatters/decimal.formatter';
import { WordTextFormatter } from '../../shared/formatters/word-text.formatter';
import { DateTimeFormatter } from '../../shared/formatters/datetime.formatter';
import { TimeFormatter, TimeFormat } from '../../shared/formatters/time.formatter';
import { DoNothingFormatter } from '../../shared/formatters/do-nothing.formatter';
import { NonNumericFormatter } from '../../shared/formatters/non-numeric.formatter';


@Injectable({
    providedIn: 'root',
  })
export class FormattersService {
    private formatters = new Map<string, Map<string, IFormatter>>();

    constructor(private log: Logger, private localeService: LocaleService) {
        const USFormatters = new Map<string, IFormatter>();
        const defaultPhoneFormatter = new PhoneUSFormatter();

        USFormatters.set('phone', defaultPhoneFormatter);
        this.formatters.set('en-us', USFormatters);
        this.formatters.set('us', USFormatters);

        const CAFormatters = new Map<string, IFormatter>();
        CAFormatters.set('phone', new PhoneCAFormatter());
        CAFormatters.set('datetime', new DateTimeCAFormatter());
        CAFormatters.set('postalcode', new PostalCodeCAFormatter());

        // Some screens are dependent on 'ca' value, so don't change.  If you have other
        // ca formatters that are language specific, add a second entry in the map for them.
        this.formatters.set('ca', CAFormatters);
        this.formatters.set('en-ca', CAFormatters);

        const UKFormatters = new Map<string, IFormatter>();
        UKFormatters.set('datetime', new DateTimeCAFormatter());
        this.formatters.set('gb', UKFormatters);
        this.formatters.set('en-gb', UKFormatters);

        // If there isn't a specific formatter for a given locale, we fall back these
        const NOLOCALEFormatters = new Map<string, IFormatter>();
        this.formatters.set('NO-LOCALE', NOLOCALEFormatters);
        // Default formatters if no locale specific
        const numericFormatter = new NumericFormatter();
        NOLOCALEFormatters.set('numeric', numericFormatter);
        NOLOCALEFormatters.set('nonnumerictext', new NonNumericFormatter());
        NOLOCALEFormatters.set('numerictext', numericFormatter);
        NOLOCALEFormatters.set('giftcode', new GiftCodeFormatter());
        // Use USD formatter as default
        NOLOCALEFormatters.set('money', new MoneyFormatter(localeService));
        NOLOCALEFormatters.set('phone', numericFormatter);
        NOLOCALEFormatters.set('percent', new PercentageFormatter());
        NOLOCALEFormatters.set('percentint', new PercentageFormatter(PercentageFormatter.INTEGER_MODE));
        NOLOCALEFormatters.set('postalcode', new PostalCodeFormatter());
        NOLOCALEFormatters.set('uspostalcode', numericFormatter);
        NOLOCALEFormatters.set('income', new IncomeFormatter());
        NOLOCALEFormatters.set('stateidnumber', new StateIDNumberFormatter());
        NOLOCALEFormatters.set('decimal', new DecimalFormatter());
        NOLOCALEFormatters.set('wordtext', new WordTextFormatter());
        NOLOCALEFormatters.set('datetime', new DateTimeFormatter());
        NOLOCALEFormatters.set('hour', new TimeFormatter(TimeFormat.HOUR));
        NOLOCALEFormatters.set('minsec', new TimeFormatter(TimeFormat.MIN_SEC));
        NOLOCALEFormatters.set('monthdate', new DateTimeFormatter('MM/dd'));
    }

    getFormatter(name: string): IFormatter {

        const locale = this.localeService.getLocale();
        if (name && locale) {
            const lname = name.toLowerCase();
            const llocale = locale.toLowerCase();
            // see if we have a validator map for the current locale
            //  and that locale has the validator we need
            if (this.formatters.get(llocale) && this.formatters.get(llocale).get(lname)) {
                return this.formatters.get(llocale).get(lname);
            }

            if (this.formatters.get('NO-LOCALE') && this.formatters.get('NO-LOCALE').get(lname)) {
                return this.formatters.get('NO-LOCALE').get(lname);
            }
        }

        if ( name ) {
            this.log.debug(`No formatter found for locale '${locale}' formatter name '${name}'. Using a 'Do Nothing' formatter`);
        }
        return new DoNothingFormatter();
    }

    setFormatter(name: string, formatter: IFormatter, locale?: string) {
        if (!locale) {
            locale = 'NO-LOCALE';
        }

        this.formatters.get(locale).set(name, formatter);
    }
}
