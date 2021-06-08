import { Component, OnInit, Input, Output, EventEmitter, Optional, ViewChild } from '@angular/core';
import createAutoCorrectedDatePipe from 'text-mask-addons/dist/createAutoCorrectedDatePipe';
import { FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatDatepickerInputEvent, MatInput, MatDatepicker } from '@angular/material';
import { DateUtils, DatePartPositions } from '../../utils/date.utils';

@Component({
    selector: 'app-dynamic-date-form-field',
    templateUrl: './dynamic-date-form-field.component.html',
    styleUrls: ['./dynamic-date-form-field.component.scss']
  })
  export class DynamicDateFormFieldComponent implements OnInit {
    protected static readonly DEFAULT_MASK = [/\d/, /\d/, '/', /\d/, /\d/, '/', /\d/, /\d/, /\d/, /\d/];
    protected static readonly dateMasks: Map<string, DateFormatEntry> = new Map([
        ['date', { mask: DynamicDateFormFieldComponent.DEFAULT_MASK, format: 'MM/dd/yyyy',
                   datePipe: createAutoCorrectedDatePipe('mm/dd/yyyy') }],
        ['noyeardate', { mask: [/\d/, /\d/, '/', /\d/, /\d/], format: 'MM/dd',
                         datePipe: createAutoCorrectedDatePipe('mm/dd') }],
        ['datemmddyy', { mask: [/\d/, /\d/, '/', /\d/, /\d/, '/', /\d/, /\d/], format: 'MM/dd/yy',
                         datePipe: createAutoCorrectedDatePipe('mm/dd/yy') }],
        ['dateddmmyy', { mask: [/\d/, /\d/, '/', /\d/, /\d/, '/', /\d/, /\d/], format: 'dd/MM/yy',
                         datePipe: createAutoCorrectedDatePipe('dd/mm/yy') }],
        ['dateddmmyyyy', { mask: DynamicDateFormFieldComponent.DEFAULT_MASK, format: 'dd/MM/yyyy',
                         datePipe: createAutoCorrectedDatePipe('dd/mm/yyyy') }]
    ]);

    @ViewChild(MatInput) field: MatInput;
    @ViewChild('picker') datepicker: MatDatepicker<Date>;

    @Input() type: string;
    @Input() value: string;
    @Input() placeholder: string;
    @Input() isPrompt = false;
    @Input() hideCalendar = false;
    @Input() required = false;
    @Input() hintText = '';
    @Input() controlName: string;
    @Input() hiddenControl: string;
    @Input() keyboardLayout: string;
    @Input() form: FormGroup;
    @Input() minDate: Date;
    @Input() maxDate: Date;
    @Input() startAtDate: Date;

    @Output() valueChange = new EventEmitter<any>();

    dateMask = DynamicDateFormFieldComponent.dateMasks.get('date').mask;
    autoCorrectedDatePipe = DynamicDateFormFieldComponent.dateMasks.get('date').datePipe;
    format = DynamicDateFormFieldComponent.dateMasks.get('date').format;
    dateValue: Date;
    private datePartPos: DatePartPositions;
    minimumDate: Date;
    maximumDate: Date;
    startAt: Date;

    constructor(@Optional() private datePipe: DatePipe) {
    }

    ngOnInit() {
        if (this.type) {
            const lowerType = this.type.toLowerCase();
            this.dateMask = DynamicDateFormFieldComponent.dateMasks.get(lowerType).mask;
            this.autoCorrectedDatePipe = DynamicDateFormFieldComponent.dateMasks.get(lowerType).datePipe;
            this.format = DynamicDateFormFieldComponent.dateMasks.get(lowerType).format;
            this.datePartPos = DateUtils.datePartPositions(this.format);
        }

        if (this.minDate) {
          this.minimumDate = new Date(this.minDate);
        }

        if (this.maxDate) {
          this.maximumDate = new Date(this.maxDate);
        }

        if (this.startAtDate) {
          this.startAt = new Date(this.startAtDate);
        }

    }

    public focus(): void {
        this.field.focus();
    }

    public onDateEntered(): void {
      if (this.value) {
        this.value = this.value.replace(/_/g, '');
        this.valueChange.emit(this.value);
      }
    }

    public onDatePicked(event: MatDatepickerInputEvent<Date>): void {
      this.value = this.datePipe.transform(event.value, this.format);
      this.form.get(this.controlName).setValue(this.value);
      this.valueChange.emit(this.value);
    }

    public onDateChange(): void {
      const dateParts = this.value.split('/');
      if (dateParts.length > 1) {
        // JavaScript counts months from 0 to 11. January is 0. December is 11.
        const month = Number(dateParts[this.datePartPos.monthPos]) - 1;
        const dayOfMonth = Number(dateParts[this.datePartPos.dayOfMonthPos]);
        let year = (new Date()).getFullYear();

        if (dateParts.length > 2) {
          year = Number(dateParts[this.datePartPos.yearPos]);
        }

        year = DateUtils.normalizeDateYear(month, dayOfMonth, year);
        const dateValue = new Date(year, month, dayOfMonth, 0, 0, 0, 0);
//        console.log(`dateValue = ${dateValue}, year: ${year}, month: ${month}, dayOfMonth: ${dayOfMonth}`);

        if (this.hiddenControl) {
            this.form.get(this.hiddenControl).setValue(dateValue);
        }

        // if it is a valid time emit the value
        if (dateValue && !isNaN(dateValue.getTime())) {
          this.onDateEntered();
        }
      }
    }
  }

  interface DateFormatEntry {
      mask: Array<string|RegExp>;
      format: string;
      datePipe: any;
  }
