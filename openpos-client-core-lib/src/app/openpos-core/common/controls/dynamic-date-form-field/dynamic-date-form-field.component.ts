import { Component, OnInit, Input, Output, EventEmitter, Optional } from "@angular/core";
import createAutoCorrectedDatePipe from "text-mask-addons/dist/createAutoCorrectedDatePipe";
import { IFormElement } from "../../iformfield";
import { FormGroup, FormControl } from "@angular/forms";
import { DatePipe } from "@angular/common";
import { MatSelectChange, MatDatepickerInputEvent } from "@angular/material";


@Component({
    selector: 'app-dynamic-date-form-field',
    templateUrl: './dynamic-date-form-field.component.html',
    styleUrls: ['./dynamic-date-form-field.component.scss']
  })
  export class DynamicDateFormFieldComponent implements OnInit {
  
    @Input() type: string;
    @Input() value: string;
    @Input() placeholder: string;
    @Input() isPrompt: boolean = false;
    @Input() hideCalendar: boolean = false;
    @Input() hintText: string ='';
    @Input() controlName: string;
    @Input() hiddenControl: string;
    @Input() form: FormGroup;

    @Output() valueChange = new EventEmitter<any>();
  
    dateMask = [/\d/, /\d/, '/', /\d/, /\d/,'/', /\d/, /\d/, /\d/, /\d/];
    autoCorrectedDatePipe = createAutoCorrectedDatePipe('mm/dd/yyyy');
    format = 'MM/dd/yyyy';
    dateValue: Date;
  
    constructor(@Optional() private datePipe: DatePipe) {}
  
    ngOnInit() {
        if(this.type === 'NoYearDate' || this.type === 'NOYEARDATE') {
            this.dateMask = [/\d/, /\d/, '/', /\d/, /\d/];
            this.autoCorrectedDatePipe = createAutoCorrectedDatePipe('mm/dd');
            this.format = 'MM/dd';
        }
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
      let dates = this.value.split("/");
      if(dates.length > 1){
        //JavaScript counts months from 0 to 11. January is 0. December is 11.
        let month = parseInt(dates[0]) - 1; 
        let day = parseInt(dates[1]);
        let year = (new Date()).getFullYear;
        if(dates.length > 2) {
          let year = parseInt(dates[2]);      
          this.dateValue = new Date(year, month, day, 0, 0, 0, 0);
        } else {
          let todayYear = (new Date()).getFullYear();
          this.dateValue = new Date(todayYear, month, day, 0, 0, 0, 0);
        }
      }
    }
  
  }
  
  