import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-rounded-input',
  templateUrl: './rounded-input.component.html',
  styleUrls: ['./rounded-input.component.scss']
})
export class RoundedInputComponent {
  @Input()
  iconName: string;

  @Input()
  placeholder: string;

  @Input()
  inputType: string;

  @Input()
  value: string = null;

  @Output()
  valueChange = new EventEmitter<string>();

  onKeyUp(value: string){
    this.valueChange.emit(value);
  }
}
