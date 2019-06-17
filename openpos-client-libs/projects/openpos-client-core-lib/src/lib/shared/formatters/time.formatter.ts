import { IFormatter } from './formatter.interface';
import { DatePipe } from '@angular/common';

export enum TimeFormat {
    HOUR,
    MIN_SEC
}

export class TimeFormatter implements IFormatter {

    private hourRegex = /^[1-9]$|^1[0-2]$/;
    private minSecRegex = /^[0-9]$|^[0-5][0-9]$/;

    constructor(public format: TimeFormat) {
    }

    formatValue(value: string): string {
        if (!value) {
            return '';
        }
        return value;
    }

    unFormatValue(value: string): string {
        return value;
    }

    allowKey(key: string, newValue: string) {
        let result = false;
        switch (this.format) {
            case TimeFormat.HOUR:
                result = this.hourRegex.test(newValue);
                break;
            case TimeFormat.MIN_SEC:
                result = this.minSecRegex.test(newValue);
                break;
        }

        return result;
    }
}
