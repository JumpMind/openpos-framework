import { IFormatter } from './formatter.interface';

export class PhoneUSFormatter implements IFormatter {
    private newValueFilter = /^[2-9]$|^[2-9][0-8]$|^[2-9][0-8][0-9]$|^[2-9][0-8][0-9][2-9]$|^[2-9][0-8][0-9][2-9][0-9]$|^[2-9][0-8][0-9][2-9][0-9]{2}$|^[2-9][0-8][0-9][2-9][0-9]{3}$|^[2-9][0-8][0-9][2-9][0-9]{4}$|^[2-9][0-8][0-9][2-9][0-9]{5}$|^[2-9][0-8][0-9][2-9][0-9]{6}$/;
    constructor() {
    }

    formatValue(value: string): string {
        if (!value) {
            return '';
        }

        if (value.length <= 3) {
            return `(${value.slice(0)}`;
        }

        if (value.length <= 6) {
            return `(${value.slice(0, 3)}) ${value.slice(3, 6)}`;
        }

        return `(${value.slice(0, 3)}) ${value.slice(3, 6)}-${value.slice(6, 10)}`;
    }

    unFormatValue(value: string): string {
        const n = value.replace(/\D/g, '');
        return n;
    }

    allowKey(key: string, newValue: string) {
        return this.newValueFilter.test(newValue);
    }
}
