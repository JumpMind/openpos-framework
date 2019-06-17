import { Component, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-icon-fab-button',
  templateUrl: './icon-fab-button.component.html',
  styleUrls: ['./icon-fab-button.component.scss']
})
export class IconFabButtonComponent {

  @Input() disabled: boolean;
  @Input() iconName: string;
  @Input() color: string;
  @Output() buttonClick = new EventEmitter();
  constructor() {
    this.disabled = false;
  }

  clickFn() {
    this.buttonClick.emit(true);
  }
}
