import {IFormatter} from './formatter.interface';

export class WeightFormatter implements IFormatter {
    static readonly FILTER_REGEX = /^\d*(\.\d{0,2})?$/;

    allowKey(key: string, newValue: string): boolean {
        return WeightFormatter.FILTER_REGEX.test(newValue);
    }

    formatValue(value: string): string {
        return !value && value != '0' ? '' : value;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
