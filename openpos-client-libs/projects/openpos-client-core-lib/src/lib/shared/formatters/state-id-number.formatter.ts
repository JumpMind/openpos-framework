import { IFormatter } from './formatter.interface';

export class StateIDNumberFormatter implements IFormatter {
    static readonly FILTER_REGEX = /^[\da-zA-Z\* ]$/; // allow alphabets, digits, space and asterisk


    allowKey(key: string, newValue: string): boolean {
        return StateIDNumberFormatter.FILTER_REGEX.test(key);
    }

    formatValue(value: string): string {
        if (!value) {
            return '';
        } else {
            return value;
        }
    }

    unFormatValue(value: string): string {
        return value;
    }
}
