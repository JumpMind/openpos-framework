import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-time-chooser',
  templateUrl: './time-chooser.component.html',
  styleUrls: ['./time-chooser.component.scss']
})
export class TimeChooserComponent implements OnInit {

  @Input() value: string;
  @Input() formGroup: FormGroup;
  @Input() controlName: string;
  @Input() label: string;
  @Input() required: boolean;
  @Input() keyboardLayout: string;
  @Input() iconName: string;
  @Output() change = new EventEmitter<string>();

  hours: string;
  minutes: string;
  seconds: string;
  amPm = 'am';

  constructor() { }

  ngOnInit() {
    // if time value is passed in, interpret string of format HH:MM:SS to correctly formatted time
    if (this.value) {
      const time = this.value.split(':');
      const hourValue = time[0];
      const minValue = time[1];
      const secValue = time[2];

      let hourAsNum = Number.parseInt(hourValue, 10);
      if (hourAsNum === 12) {
        this.amPm = 'pm';
        this.hours = hourValue;
      } else if (hourAsNum === 24) {
        this.amPm = 'am';
        hourAsNum -= 12;
        this.hours = hourAsNum.toString();
      } else if (hourAsNum > 12) {
        this.amPm = 'pm';
        hourAsNum -= 12;
        this.hours = hourAsNum.toString();
      } else {
        this.amPm = 'am';
        this.hours = hourValue;
      }
      this.minutes = minValue;
      this.seconds = secValue;

      this.value = this.formatTime();
    }
  }

  onTimeChange() {
    this.value = this.formatTime();
    this.formGroup.get(this.controlName).setValue(this.value);
    this.change.emit(this.value);
  }

  private formatTime(): string {
    const h24 = this.convertTo24();

    const h = this.padTime(h24);
    const m = this.padTime(this.minutes);
    const s = this.padTime(this.seconds);

    return h.concat(':', m, ':', s);
  }

  private convertTo24(): string {
    let h24 = 0;
    if (this.hours) {
      h24 = Number.parseInt(this.hours, 10);
      if (h24 < 12 && this.amPm === 'pm') {
        h24 += 12;
      } else if (h24 === 12 && this.amPm === 'am') {
        h24 = 0;
      }
    }
    return h24.toString(10);
  }

  private padTime(input: string) {
    let output = '00';
    if (input) {
      if (input.length === 1) {
        output = '0'.concat(input);
      } else {
        output = input;
      }
    }
    return output;
  }
}
