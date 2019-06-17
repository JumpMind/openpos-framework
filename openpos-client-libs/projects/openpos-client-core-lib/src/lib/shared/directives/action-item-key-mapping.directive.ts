import { Directive, Input, Renderer2, ElementRef, OnDestroy, EventEmitter, Output } from '@angular/core';
import { KeyPressProvider } from '../providers/keypress.provider';
import { Subscription } from 'rxjs';
import { Configuration } from '../../configuration/configuration';
import { SessionService } from '../../core/services/session.service';
import { IActionItem } from '../../core/interfaces/action-item.interface';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[actionItem]'
})
export class ActionItemKeyMappingDirective implements OnDestroy {
    @Output() actionClick = new EventEmitter();

    @Input()
    set actionItem(item: IActionItem) {
        if ( this.subscription ) {
            this.subscription.unsubscribe();
        }

        this.subscription = this.keyPresses.subscribe( item.keybind, 100, event => {
            // ignore repeats
            if ( event.repeat || !Configuration.enableKeybinds ) {
                return;
            }
            if ( event.type === 'keydown') {
                this.renderer.addClass(this.el.nativeElement, 'key-mapping-active');
                if (this.actionClick.observers !== null && this.actionClick.observers.length > 0) {
                    this.actionClick.emit();
                } else {
                    this.session.onAction(item);
                }
                event.preventDefault();
            } else if ( event.type === 'keyup') {
                this.renderer.removeClass(this.el.nativeElement, 'key-mapping-active');
            }
        });
    }

    private subscription: Subscription;

    constructor(
        private renderer: Renderer2,
        private el: ElementRef,
        private session: SessionService,
        private keyPresses: KeyPressProvider) {

    }

    ngOnDestroy(): void {
        if ( this.subscription ) {
            this.subscription.unsubscribe();
        }
    }

}
