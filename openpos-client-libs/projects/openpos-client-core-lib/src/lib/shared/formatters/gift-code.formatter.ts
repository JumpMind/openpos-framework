import { IFormatter } from './formatter.interface';

export class GiftCodeFormatter implements IFormatter {
    static readonly MAX_LENGTH = 8;
    static readonly FILTER_REGEX = /^[b-df-hj-mB-DF-HJ-M]{1,8}$/;
    locale?: string;


    allowKey(key: string, newValue: string): boolean {
        if (newValue.length > GiftCodeFormatter.MAX_LENGTH) {
            return false;
        }
        return GiftCodeFormatter.FILTER_REGEX.test(newValue);
    }

    formatValue(value: string): string {
        return value.toUpperCase();
    }

    unFormatValue(value: string): string {
        return value;
    }
}
