import { Component, Input, OnInit, DoCheck, OnChanges } from '@angular/core';
import { LocaleService } from '../../../core/services/locale.service';
import { CurrencyPipe } from '@angular/common';

const numberMatchWithRange = /^(?<lowerbound>\-?\d+(\.\d+)?)(\s*-\s*(?<upperbound>\-?\d+(\.\d+)?))?$/;

interface CurrencyValue {
    textBeforeSymbol: string;
    textAfterSymbol: string;
    symbolText: string;
    isNegative: boolean;
}

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
export class CurrencyTextComponent implements OnChanges {

    private euRegex = /,\d\d$/;

    @Input()
    amountText: string | number;

    @Input()
    symbol: string;

    lowerBound: CurrencyValue = {
        isNegative: false,
        symbolText: '',
        textAfterSymbol: '',
        textBeforeSymbol: ''
    };

    upperBound?: CurrencyValue;

    constructor(private localeService: LocaleService) {}

    ngOnChanges(): void {
        this.upperBound = undefined;

        let localAmtText = this.amountText || (typeof(this.amountText) === 'number') ? this.amountText.toString().trim() : null;
        if (localAmtText) {
            const rangeMatch = numberMatchWithRange.exec(localAmtText);

            if (rangeMatch && rangeMatch.groups) {
                const lower = rangeMatch.groups.lowerbound;
                const upper = rangeMatch.groups.upperbound;

                if (lower) {
                    this.lowerBound = this._makeCurrencyValue(lower);
                }

                if (upper) {
                    this.upperBound = this._makeCurrencyValue(upper);
                }
            } else {
                this.lowerBound = {
                    isNegative: false,
                    symbolText: '',
                    textAfterSymbol: localAmtText,
                    textBeforeSymbol: ''
                };
            }
        } else {
            this.lowerBound = {
                isNegative: false,
                symbolText: '',
                textAfterSymbol: '',
                textBeforeSymbol: ''
            };
        }
    }

    private _makeCurrencyValue(localAmtText: string): CurrencyValue {
        const locale = this.localeService.getLocale();
        const targetSymbol = this.symbol || this.localeService.getConstant('currencySymbol');

        const isNegative = localAmtText.indexOf('-') >= 0;
        const hasParens = localAmtText.indexOf('(') >= 0;

        var textBeforeSymbol: string;
        var textAfterSymbol: string;
        var symbolText: string;
        
        // CurrencyPipe does not like text starting with open paren
        localAmtText = this.normalizeNegativeAmount(localAmtText);

        if (!this.euRegex.test(localAmtText)) {
            localAmtText = this.removeCommas(localAmtText);
        } else {
            localAmtText = this.formatEuText(localAmtText);
        }

        let existingSymbolIdx = localAmtText.indexOf(targetSymbol);
        if (existingSymbolIdx < 0) {
            // No symbol in given text, use currency pipe to insert one
            const currencyCode = this.localeService.getConstant('currencyCode');
            const currencyPipe = new CurrencyPipe(locale);
            try {
                localAmtText = currencyPipe.transform(localAmtText, currencyCode, 'symbol-narrow', '1.2', locale);
            } catch {
                console.log(`Invalid Currency text ${localAmtText}`);
            }
            existingSymbolIdx = localAmtText.indexOf(targetSymbol);
        }

        if (existingSymbolIdx >= 0) {
            textBeforeSymbol = localAmtText.substring(0, existingSymbolIdx);
            textAfterSymbol = localAmtText.substring(existingSymbolIdx + targetSymbol.length, localAmtText.length);
            symbolText = localAmtText.substring(existingSymbolIdx, existingSymbolIdx + targetSymbol.length);
        } else {
            // expected currency symbol not there
            symbolText = '';
            textAfterSymbol = localAmtText;
            textBeforeSymbol = '';
        }

        // If we were originally given a neg. number with parens, put them back since it looks like
        // currency pipe doesn't handle it :-/
        if (hasParens && textBeforeSymbol && textBeforeSymbol.indexOf('-') >= 0) {
            textBeforeSymbol = textBeforeSymbol.replace('-', '(');
            textAfterSymbol = `${textAfterSymbol})`;
        }

        return {
            isNegative,
            symbolText,
            textAfterSymbol,
            textBeforeSymbol
        };
    }

    protected removeCommas(text: string): string {
        return text.replace(/,/g, '');
    }

    protected formatEuText(text: string): string {
        text = text.replace(/,/g, '.');
        return text.replace(/\s/g, '');
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
