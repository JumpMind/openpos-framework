import { Input, Component, OnInit, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { DatePartChooserDialogComponent } from '../date-part-chooser-dialog/date-part-chooser-dialog.component';
import { IDatePartChooserField } from '../../../../core/interfaces/date-part-chooser-field.interface';
import { FormattersService } from '../../../../core/services/formatters.service';

@Component({
    selector: 'app-date-part-chooser-field',
    templateUrl: './date-part-chooser-field.component.html',
    styleUrls: ['./date-part-chooser-field.component.scss']
})

export class DatePartChooserFieldComponent implements OnInit, AfterViewInit {

    @Input() formGroup: FormGroup;
    @Input() controlName: string;
    @Input() required: boolean;
    @Input() placeholder: string;
    @Input() iconName: string;
    @Input() formatterName: string;
    @Input() deleteAllowed = true;
    @Output() delete = new EventEmitter<IDatePartChooserField>();
    @Input() deleteIcon = 'delete_forever';
    @Input() modifyAllowed = true;
    @Input() model: IDatePartChooserField;
    @Input() hideButtons = false;

    @Output() change = new EventEmitter<any>();

    constructor(protected formatters: FormattersService,
        protected dialog: MatDialog,
    ) {

    }

    ngAfterViewInit(): void {
    }

    ngOnInit(): void {
        if (this.formGroup && this.model.dayOfMonth && this.model.month) {
            this.setFieldValue(this.formatForDisplay());
        }
    }

    private isLeapYear(year: number): boolean {
        return ((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0);
    }

    formatForDisplay(): string {
        const d: Date = new Date();
        d.setDate(1);  // Default to first of the month
        if (this.model.month) {
            d.setMonth(this.model.month - 1);
        }
        if (this.model.dayOfMonth) {
            if (this.model.month === 2 && this.model.dayOfMonth === 29) {
                // adjust year to nearest leap year
                let year = d.getFullYear();
                while (! this.isLeapYear(year) && year > 0) {
                    year--;
                }
                d.setFullYear(year);
            }
            d.setDate(this.model.dayOfMonth);
        }
        if (this.model.year) {
            d.setFullYear(this.model.year);
        }

        return this.formatters.getFormatter(this.formatterName).formatValue(d);
    }

    private setFieldValue(value: string) {
        const patchGroup = {};
        patchGroup[this.controlName] = value;

        setTimeout(() => this.formGroup.patchValue(patchGroup), 0);
    }

    onDelete() {
        if (this.deleteAllowed) {
            this.setFieldValue(null);
        }
        this.delete.emit(this.model);
    }

    openDatePartChooser() {
        if (! this.modifyAllowed) {
            return;
        }
        const today = new Date();
        const dateParts = {
            month: today.getMonth() + 1,
            dayOfMonth: today.getDate(),
            year: null
        };

        if (this.model.month) {
            dateParts.month = this.model.month;
            dateParts.dayOfMonth = 1;
        }

        if (this.model.dayOfMonth) {
            dateParts.dayOfMonth = this.model.dayOfMonth;
        }

        if (this.model.year) {
            dateParts.year = this.model.year;
        }

        const dialogRef = this.dialog.open(DatePartChooserDialogComponent, {
            width: '750px',
            data: {
              dateParts,
              mode: this.model.mode,
              title: this.model.popupTitle,
              disableClose: false,
              autoFocus: false
           }
        });

        dialogRef.afterClosed().subscribe(() => {
            this.model.month = dateParts.month;
            this.model.dayOfMonth = dateParts.dayOfMonth;
            this.model.year = dateParts.year;
            this.setFieldValue(this.formatForDisplay());
            this.change.emit(this.model);
        });

    }
}
