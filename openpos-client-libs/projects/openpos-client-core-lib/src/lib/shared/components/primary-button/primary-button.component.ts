import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';

 /**
  * Example of usage:
  * <example-url>/openpos-client-demo/#/componentdemo/primarybutton</example-url>
  */
@Component({
  selector: 'app-primary-button',
  templateUrl: './primary-button.component.html',
  styleUrls: ['./primary-button.component.scss']
})
export class PrimaryButtonComponent implements OnInit {

  @Input() disabled: boolean;
  @Input() type: string;
  @Output() buttonClick = new EventEmitter();
  constructor() {
    this.disabled = false;
  }

  ngOnInit() {
  }

  clickFn() {
    this.buttonClick.emit(true);
  }
}
