import {Component, Input, Output, EventEmitter, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs';
import { DomEventManager } from '../../../core/services/dom-event-manager.service';
import { Configuration } from '../../../configuration/configuration';

@Component({
    selector: 'app-option-button',
    templateUrl: './option-button.component.html',
    styleUrls: ['./option-button.component.scss']
  })
export class OptionButtonComponent implements OnDestroy{
    @Input() disabled = false;
    @Input() inputType: string;
    @Input() optionTitle: string;
    @Input() optionIcon: string;
    @Input() optionSize: string;
    @Input() keybind: string;
    @Output() buttonClick = new EventEmitter();

    @ViewChild('button') button;

    private subscription: Subscription;

    constructor(private elementRef: ElementRef, private domEventManager: DomEventManager) {
      this.subscription = this.domEventManager.createEventObserver(this.elementRef.nativeElement, 'focus').subscribe(() => {
        this.button.focus();
      });
    }

    clickFn() {
        this.buttonClick.emit(true);
    }

    public keybindsEnabled(): boolean {
        return Configuration.enableKeybinds && !!this.keybind;
    }

    ngOnDestroy(): void {
        if(this.subscription){
            this.subscription.unsubscribe();
        }
    }
}
