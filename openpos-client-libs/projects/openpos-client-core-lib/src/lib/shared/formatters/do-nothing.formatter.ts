import { IFormatter } from './formatter.interface';

export class DoNothingFormatter implements IFormatter {
    locale?: string;

    formatValue(value: string): string {
        if (!value) { return ''; }
        return value;
    }


    unFormatValue(value: string): string {
        return value;
    }

    allowKey(key: string, newValue: string): boolean {
        return true;
    }
}
