import { Component, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-secondary-button',
  templateUrl: './secondary-button.component.html',
  styleUrls: ['./secondary-button.component.scss']
})
export class SecondaryButtonComponent {

  @Input() disabled: boolean;
  @Input() inputType = 'button';
  @Output() buttonClick = new EventEmitter();
  constructor() {
    this.disabled = false;
  }

  clickFn() {
    this.buttonClick.emit(true);
  }
}
