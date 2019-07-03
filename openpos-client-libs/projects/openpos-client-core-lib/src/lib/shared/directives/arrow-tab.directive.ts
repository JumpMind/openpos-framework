import { Directive, ViewChildren, ElementRef, QueryList, OnDestroy, ContentChildren } from '@angular/core';
import { ArrowTabItemDirective } from './arrow-tab-item.directive';
import { Subscription } from 'rxjs';
import { KeyPressProvider } from '../providers/keypress.provider';

@Directive({
    selector: '[appArrowTab]'
})
export class ArrowTabDirective implements OnDestroy {
    @ContentChildren(ArrowTabItemDirective, {read: ElementRef})
    buttons: QueryList<ElementRef>;

    private _subscription: Subscription;

    constructor( keyPresses: KeyPressProvider) {
        this._subscription = keyPresses.subscribe( 'ArrowUp', 1, (event: KeyboardEvent) => {

            if ( event.repeat || event.type !== 'keydown' ) {
                return;
            }
            const index = this.buttons.toArray().map(v => v.nativeElement).indexOf(document.activeElement);
            let newIndex = index - 1;
            while (newIndex > 0 && this.buttons.toArray()[newIndex].nativeElement.disabled) {
                newIndex--;
            }
            if ( newIndex >= 0) {
                this.buttons.toArray()[newIndex].nativeElement.focus();
            }
        });

        this._subscription.add( keyPresses.subscribe( 'ArrowDown', 1, (event: KeyboardEvent) => {
            if ( event.repeat || event.type !== 'keydown' ) {
                return;
            }
            const index = this.buttons.toArray().map(v => v.nativeElement).indexOf(document.activeElement);
            let newIndex = index + 1;
            while (newIndex < this.buttons.length - 1 && this.buttons.toArray()[newIndex].nativeElement.disabled) {
                newIndex++;
            }
            if ( newIndex < this.buttons.length) {
                this.buttons.toArray()[newIndex].nativeElement.focus();
            }
        }));
    }

    ngOnDestroy(): void {
        if (this._subscription) {
            this._subscription.unsubscribe();
        }
    }
}
