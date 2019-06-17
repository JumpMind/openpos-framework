import { IFormatter } from './formatter.interface';

export class WordTextFormatter implements IFormatter {
    static readonly FILTER_REGEX = /^[a-zA-Z0-9]$/;


    allowKey(key: string, newValue: string): boolean {
        return WordTextFormatter.FILTER_REGEX.test(key);
    }

    formatValue(value: string): string {
        return value;
    }

    unFormatValue(value: string): string {
        return value;
    }
}
