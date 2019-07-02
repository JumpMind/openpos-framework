import { Component, Input, OnInit, DoCheck } from '@angular/core';
import { LocaleService } from '../../../core/services/locale.service';
import { CurrencyPipe } from '@angular/common';

/**
 * Component used to display a currency text amount in a locale-specific format.
 * This component allows for differing the style of the currency symbol from the style
 * used for the rest of the currency text.  By default the currency symbol will appear
 * in the same position that a superscript would appear.
 */
@Component({
    selector: 'app-currency-text',
    templateUrl: './currency-text.component.html',
    styleUrls: ['./currency-text.component.scss']
})
export class CurrencyTextComponent implements DoCheck {

    @Input()
    amountText: string | number;

    @Input()
    symbol: string;

    textBeforeSymbol: string;
    textAfterSymbol: string;
    symbolText: string;

    constructor(private localeService: LocaleService) {}

    ngDoCheck(): void {
        const locale = this.localeService.getLocale();
        const targetSymbol = this.symbol || this.localeService.getConstant('currencySymbol');

        let localAmtText = this.amountText || (typeof(this.amountText) === 'number') ? this.amountText.toString().trim() : null;
        if (localAmtText) {
            const hasNegSign = localAmtText.indexOf('-') >= 0;
            const hasParens = localAmtText.indexOf('(') >= 0;
            // CurrencyPipe does not like text starting with open paren
            localAmtText = this.normalizeNegativeAmount(localAmtText);
            localAmtText = this.removeCommas(localAmtText);
            let existingSymbolIdx = localAmtText.indexOf(targetSymbol);
            if (existingSymbolIdx < 0) {
                // No symbol in given text, use currency pipe to insert one
                const currencyCode = this.localeService.getConstant('currencyCode');
                const currencyPipe = new CurrencyPipe(locale);
                localAmtText = currencyPipe.transform(localAmtText, currencyCode, 'symbol-narrow', '1.2', locale);
                existingSymbolIdx = localAmtText.indexOf(targetSymbol);
            }

            if (existingSymbolIdx >= 0) {
                this.textBeforeSymbol = localAmtText.substring(0, existingSymbolIdx);
                this.textAfterSymbol = localAmtText.substring(existingSymbolIdx + targetSymbol.length, localAmtText.length);
                this.symbolText = localAmtText.substring(existingSymbolIdx, existingSymbolIdx + targetSymbol.length);
            } else {
                // expected currency symbol not there
                this.symbolText = '';
                this.textAfterSymbol = localAmtText;
                this.textBeforeSymbol = '';
            }

            // If we were originally given a neg. number with parens, put them back since it looks like
            // currency pipe doesn't handle it :-/
            if (hasParens && this.textBeforeSymbol && this.textBeforeSymbol.indexOf('-') >= 0) {
                this.textBeforeSymbol = this.textBeforeSymbol.replace('-', '(');
                this.textAfterSymbol = `${this.textAfterSymbol})`;
            }
        } else {
            this.symbolText = '';
            this.textAfterSymbol = '';
            this.textBeforeSymbol = '';
        }
    }

    protected removeCommas(text: string): string {
        return text.replace(/,/g, '');
    }

    protected normalizeNegativeAmount(text: string): string {
        return text.indexOf('(') === 0 ? `-${text.replace('(', '').replace(')', '')}` : text;
    }

    protected locateNegativeSign(text: string): number {
        let loc = text.indexOf('-');
        if (loc < 0) {
            loc = text.indexOf('(');
        }

        return loc;
    }

}
