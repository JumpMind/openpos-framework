import { IFormatter } from './formatter.interface';

export class PostalCodeFormatter implements IFormatter {
    static readonly CHAR_REGEX = /[^0-9a-zA-Z\-]/g;
    static readonly FILTER_REGEX = /^[0-9a-zA-Z\-]{1,20}$/;

    allowKey(key: string, newValue: string): boolean {
        return PostalCodeFormatter.FILTER_REGEX.test(newValue);
    }

    formatValue(value: string): string {
        // remove any invalid chars
        let returnValue = value.replace(PostalCodeFormatter.CHAR_REGEX, '');
        // trim to max len of 20
        if (returnValue && returnValue.length > 20) {
            returnValue = returnValue.substring(0, 20);
        }
        return returnValue;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
