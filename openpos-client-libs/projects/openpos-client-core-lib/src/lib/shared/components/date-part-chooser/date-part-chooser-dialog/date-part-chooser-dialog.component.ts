import { Input, Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { DatePartChooserMode, IDateParts } from '../../../../core/interfaces/date-part-chooser-field.interface';

@Component({
    selector: 'app-date-part-chooser-dialog',
    templateUrl: './date-part-chooser-dialog.component.html',
    styleUrls: ['./date-part-chooser-dialog.component.scss']
})

export class DatePartChooserDialogComponent implements OnInit {

    @Input() title: string;
    @Input() closeButton: boolean;
    @Input() dateParts: IDateParts;
    @Input() mode: DatePartChooserMode;

    constructor(@Inject(MAT_DIALOG_DATA) public data: any,
        public dialogRef: MatDialogRef<DatePartChooserDialogComponent>) {
        if (data) {
            if (data.dateParts) {
                this.dateParts = <IDateParts> data.dateParts;
            }
            if (data.title) {
                this.title = data.title;
            }
            if (! data.disableClose) {
                this.closeButton = true;
            }
            this.mode = data.mode;
        }

    }

    onDateChange($event: IDateParts) {
        this.dateParts.month = $event.month;
        this.dateParts.dayOfMonth = $event.dayOfMonth;
        this.dateParts.year = $event.year;
    }

    ngOnInit(): void {
    }

}
