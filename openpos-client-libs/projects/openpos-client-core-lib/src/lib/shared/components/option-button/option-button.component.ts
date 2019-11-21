import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'app-option-button',
    templateUrl: './option-button.component.html',
    styleUrls: ['./option-button.component.scss']
  })
export class OptionButtonComponent {
    @Input() disabled = false;
    @Input() inputType: string;
    @Input() optionTitle: string;
    @Input() optionIcon: string;
    @Input() optionSize: string;
    @Output() buttonClick = new EventEmitter();

    constructor() {

    }

    clickFn() {
      this.buttonClick.emit(true);
    }
}
