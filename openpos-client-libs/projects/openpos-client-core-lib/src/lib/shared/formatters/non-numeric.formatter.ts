import { IFormatter } from './formatter.interface';

export class NonNumericFormatter implements IFormatter {
    static readonly CHAR_REGEX = /[0-9]/g;
    static readonly FILTER_REGEX = /^[^0-9]$/;


    allowKey(key: string, newValue: string): boolean {
        return NonNumericFormatter.FILTER_REGEX.test(key);
    }

    formatValue(value: string): string {
        if (!value) {
            return '';
        } else {
            // remove any invalid chars
            // convert to string JIC value passed in isn't actually a string.  Saw a case of that.
            return value.toString().replace(NonNumericFormatter.CHAR_REGEX, '');
        }
    }

    unFormatValue(value: string): string {
        return value;
    }
}
