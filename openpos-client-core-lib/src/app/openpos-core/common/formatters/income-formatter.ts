import { IFormatter } from './iformatter';

export class IncomeFormatter implements IFormatter {
    static readonly FILTER_REGEX = /^[0-9]$/;


    allowKey(key: string, newValue: string): boolean {
        return IncomeFormatter.FILTER_REGEX.test(key);
    }

    formatValue(value: string): string {
        if (! value) {
            return '';
        } else {
            let count = 1;
            let formattedValue = '';
            for (let idx = value.length - 1; idx >= 0; idx--) {
                formattedValue = `${(count % 3 === 0 && count < value.length ? ',' : '')}${value.charAt(idx)}${formattedValue}`;
                count++;
            }
            return `$${formattedValue},000`;
        }
    }

    unFormatValue(value: string): string {
        const newValue = value.replace(/,000$/, '').replace(/\$|,/g, '');
        return newValue;
    }
}
