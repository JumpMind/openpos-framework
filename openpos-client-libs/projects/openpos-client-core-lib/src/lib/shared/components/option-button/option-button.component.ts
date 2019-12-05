import { Component, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { DomEventManager } from '../../../core/services/dom-event-manager.service';

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

    @ViewChild('button') button;

    constructor(private elementRef: ElementRef, private domEventManager: DomEventManager) {
      this.domEventManager.createEventObserver(this.elementRef.nativeElement, 'focus').subscribe(() => {
        this.button.focus();
      });
    }

    clickFn() {
      this.buttonClick.emit(true);
    }
}
