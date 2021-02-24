import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';

 /**
  * Example of usage:
  * <example-url>/openpos-client-demo/#/componentdemo/accentbutton</example-url>
  */
@Component({
  selector: 'app-accent-button',
  templateUrl: './accent-button.component.html',
  styleUrls: ['./accent-button.component.scss']
})
export class AccentButtonComponent implements OnInit {

  @Input() disabled: boolean;
  @Input() type: string;
  @Output() buttonClick = new EventEmitter();
  constructor() {
    this.disabled = false;
  }

  ngOnInit() {
  }

  clickFn() {
    if (!this.disabled) {
      this.buttonClick.emit(true);
    }
  }
}
