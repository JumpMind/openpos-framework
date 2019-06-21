import { IFormatter } from './formatter.interface';
import { DatePipe } from '@angular/common';

export class DateTimeFormatter implements IFormatter {
    static DATETIME_FORMAT = 'MM/dd/yyyy hh:mm:ss a';

    private datePipe: DatePipe;


    constructor(public format?: string) {
        this.datePipe = new DatePipe('en-US');
    }

    formatValue(theDate: Date): string {
        const formattedDate = this.datePipe.transform(theDate, this.format || DateTimeFormatter.DATETIME_FORMAT);
        return formattedDate;
    }

    unFormatValue(value: string): string {
        // TODO in future if necessary
        return null;
    }

    allowKey(key: string, newValue: string) {
        // TODO in future if necessary
        return true;
    }
}
