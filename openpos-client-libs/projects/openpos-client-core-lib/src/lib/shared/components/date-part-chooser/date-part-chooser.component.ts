import { Input, Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { FabToggleGroupComponent } from '../fab-toggle-group/fab-toggle-group.component';
import { DatePartChooserMode, IDateParts } from '../../../core/interfaces/date-part-chooser-field.interface';

@Component({
    selector: 'app-date-part-chooser',
    templateUrl: './date-part-chooser.component.html',
    styleUrls: ['./date-part-chooser.component.scss']
})

export class DatePartChooserComponent implements OnInit {

    /**
     * Array of month abbreviations.  1-based, where month at index 1 = January.
     */
    static readonly MONTHS: string[] =
        [null, 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    static readonly MONTH_DAYS: number[] =
        [null, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

    _dateChooserModeType = DatePartChooserMode;

    @Input() mode: DatePartChooserMode = DatePartChooserMode.MonthDate;
    @Input() month: number;
    @Input() dayOfMonth: number;
    @Input() year: number;
    @Output() change = new EventEmitter<IDateParts>();
    @ViewChild('monthGroup') monthGroup: FabToggleGroupComponent;

    constructor() {
    }

    ngOnInit(): void {
    }

    onDateChange($event: any) {
        this.change.emit({month: this.month, dayOfMonth: this.dayOfMonth, year: this.year});
    }

    get months() {
        return DatePartChooserComponent.MONTHS;
    }

    lastWeekMonthDays() {
        return Array.from({length: (this.monthDays - 28)}, (v, k) => k + 29);
    }

    get monthDays() {
        if (this.month && this.month >= 1 && this.month <= 12) {
            return DatePartChooserComponent.MONTH_DAYS[this.month];
        } else {
            return 31;
        }
    }
}
