import { IFormatter } from './formatter.interface';

export class AlphaNumericFormatter implements IFormatter {
    static readonly CHAR_REGEX = /[^a-zA-Z0-9]/g;
    static readonly FILTER_REGEX = /^[a-zA-Z0-9]$/;


    allowKey(key: string, newValue: string): boolean {
        return AlphaNumericFormatter.FILTER_REGEX.test(key);
    }

    formatValue(value: string): string {
        // remove any invalid chars
        // convert to string JIC value passed in isn't actually a string.  Saw a case of that.
        return value !== null ? value.toString().replace(AlphaNumericFormatter.CHAR_REGEX, '') : null;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
