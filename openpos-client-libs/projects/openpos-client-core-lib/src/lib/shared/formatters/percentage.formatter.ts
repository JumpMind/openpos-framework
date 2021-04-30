import { IFormatter } from './formatter.interface';

export class PercentageFormatter implements IFormatter {
    static readonly DECIMAL_MODE = 'decimal';
    static readonly INTEGER_MODE = 'integer';
    private static readonly FILTER_REGEX_DECIMAL_MODE = /^100$|^\d{0,2}(\.\d{1,2})?$|^\d{0,2}(\.)?$/;
    private static readonly FILTER_REGEX_INTEGER_MODE = /^100$|^\d{0,2}$/;

    mode: 'decimal'|'integer';

    constructor(mode:string = PercentageFormatter.DECIMAL_MODE) {
        this.mode = mode === PercentageFormatter.DECIMAL_MODE ? 'decimal' : 'integer';
    }

    allowKey(key: string, newValue: string): boolean {
        const match = this.mode === PercentageFormatter.INTEGER_MODE ?
            PercentageFormatter.FILTER_REGEX_INTEGER_MODE.test(newValue) :
            PercentageFormatter.FILTER_REGEX_DECIMAL_MODE.test(newValue);
        return match;
    }

    formatValue(value: string): string {
        if (!value) {
            return '';
        }
        const parts = value.split('.');

        if (parts.length > 1 && this.mode === PercentageFormatter.DECIMAL_MODE) {
            return `${parts[0]}.${parts[1].slice(0, 2)}`;
        }
        return `${parts[0]}`;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
