import { IFormatter } from './formatter.interface';
import { DatePipe } from '@angular/common';

export class DateTimeCAFormatter implements IFormatter {
    static DATETIME_FORMAT = 'dd/MM/yyyy hh:mm:ss a';

    private datePipe: DatePipe;


    constructor() {
        this.datePipe = new DatePipe('en-CA');
    }

    formatValue(theDate: Date): string {
        const formattedDate = this.datePipe.transform(theDate, DateTimeCAFormatter.DATETIME_FORMAT);
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
