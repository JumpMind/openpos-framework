import { IFormatter } from './iformatter';

export class DecimalFormatter implements IFormatter {
    static readonly FILTER_REGEX = /^\d*(\.\d*)?$/;


    allowKey(key: string, newValue: string): boolean {
        console.log(`newValue: ${newValue}`);
        return DecimalFormatter.FILTER_REGEX.test(newValue);
    }

    formatValue(value: string): string {
        return value;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
