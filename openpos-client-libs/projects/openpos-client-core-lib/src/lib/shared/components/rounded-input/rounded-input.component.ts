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
  value: string = null;

  @Output()
  valueChanged = new EventEmitter<string>();

  onKeyUp(value: string){
    this.valueChanged.emit(value);
  }
}
