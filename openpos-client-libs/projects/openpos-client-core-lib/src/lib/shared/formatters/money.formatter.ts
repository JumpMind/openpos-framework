import { IFormatter } from './formatter.interface';
import { LocaleService } from '../../core/services/locale.service';
import { CurrencyPipe } from '@angular/common';

export class MoneyFormatter implements IFormatter {

    locale?: string;

    private keyFilter = /[0-9\ | \.]/;
    private euRegex = /,\d\d$/;
    private maxLength = 14;

    constructor(public localeService: LocaleService) { }

    formatValue(value: string): string {

        if (!value && value != '0') {
            return '';
        }

        let amount = '0';
        const decimalCharSeparator = '.';
        const decimalCharLength = 2;

        if (this.euRegex.test(value.toString())) {
            value = value.replace(',', decimalCharSeparator);
        }
        if (value.length > this.maxLength) {
            value = value.replace(decimalCharSeparator, "");
            value = value.slice(0, this.maxLength - decimalCharLength) + decimalCharSeparator + value.slice(this.maxLength - decimalCharLength);
        }

        const i = value.toString().indexOf(decimalCharSeparator);
        if (i > 0) {
            const d = value.toString().slice(i + 1);
            switch (d.length) {
                case 0:
                    amount = `${value}00`;
                    break;
                case 1:
                    amount = `${value}0`;
                    break;
                case 2:
                    amount = `${value}`;
                    break;
                default:
                    amount = `${value.toString().slice(0, i + 3)}`;
            }
        } else {
            amount = `${value}${decimalCharSeparator}00`;
        }

        const locale = this.localeService.getLocale();
        const currencyCode = this.localeService.getConstant('currencyCode');
        const currencyPipe = new CurrencyPipe(locale);
        amount = currencyPipe.transform(amount, currencyCode, 'symbol-narrow', '1.2', locale);

        return amount;
    }

    unFormatValue(value: string): string {
        let n = value.replace(/[^(\d)]/g, '');

        let i = 0;
        while (i < n.length && n[i] === '0') {
            ++i;
        }

        if (i === n.length) { // all zeros
            if (i === 0 || i === 2) { // blank if we're at '00' which should be possible backspace from 0.00
                return '';
            } else {
                return `0.00`;
            }
        }

        n = n.slice(i, n.length);

        if (n.length > 2) {
            const dec = n.slice(n.length - 2, n.length);
            const whole = n.slice(0, n.length - 2);

            return `${whole}.${dec}`;
        }

        if (n.length === 1) {
            return `0.0${n}`;
        } else if (n.length === 0) {
            return '';
        }

        return `0.${n}`;
    }

    allowKey(key: string, newValue: string): boolean {
        return this.keyFilter.test(key);
    }
}
