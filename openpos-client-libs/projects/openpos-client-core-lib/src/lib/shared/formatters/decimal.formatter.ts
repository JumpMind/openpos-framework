import { IFormatter } from './formatter.interface';

export class DecimalFormatter implements IFormatter {
    // dynamic
    static readonly FILTER_REGEX = /^\d*(\.\d*)?$/;


    allowKey(key: string, newValue: string): boolean {
        return DecimalFormatter.FILTER_REGEX.test(newValue);
    }

    formatValue(value: string): string {
        if (!value && value != '0') {
            return '';
        } else {
            return value;
        }
    }

    unFormatValue(value: string): string {
        return value;
    }
}
