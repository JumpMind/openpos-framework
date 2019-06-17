import { LocaleConstantKey } from './../../core/services/locale.constants';
import { Pipe, PipeTransform } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { LocaleService } from '../../core/services/locale.service';

/**
 * Provides a way to just return the currency symbol for a given locale when
 * value is empty and display = 'symbol'.  Example: {{''|poscurrency:null:'symbol'}}.
 * Otherwise will call base currency pipe.
 */
@Pipe({ name: 'poscurrency' })
export class POSCurrencyPipe implements PipeTransform {

    constructor( private localeService: LocaleService ) {
    }

    transform(value: any, currencyCode?: string, display?: string | boolean, digitsInfo?: string, locale?: string): any {
        const locow = locale || this.localeService.getLocale();
        const currencyPipe = new CurrencyPipe(locow);
        if (! value && display === 'symbol') {
            return this.localeService.getConstant('currencySymbol', locow || 'default');
        } else {
            const lCurrencyCode = currencyCode || this.localeService.getConstant('currencyCode', locow || 'default');
            return currencyPipe.transform(value, lCurrencyCode, display, digitsInfo, locow);
        }
    }
}
