import { Component, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-warn-button',
  templateUrl: './warn-button.component.html',
  styleUrls: ['./warn-button.component.scss']
})
export class WarnButtonComponent {

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
