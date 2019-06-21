import { Component, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-icon-square-button',
  templateUrl: './icon-square-button.component.html',
  styleUrls: ['./icon-square-button.component.scss']
})
export class IconSquareButtonComponent {

  @Input() disabled: boolean;
  @Input() iconName: string;
  @Input() color: string;
  @Input() iconClass: string;
  @Output() buttonClick = new EventEmitter();
  constructor() {
    this.disabled = false;
  }

  clickFn() {
    this.buttonClick.emit(true);
  }
}
