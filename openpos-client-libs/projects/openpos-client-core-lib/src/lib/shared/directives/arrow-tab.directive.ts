import { Directive, QueryList, OnDestroy, ContentChildren } from '@angular/core';
import { ArrowTabItemDirective } from './arrow-tab-item.directive';
import { Subscription } from 'rxjs';
import { KeyPressProvider } from '../providers/keypress.provider';

@Directive({
    selector: '[appArrowTab]'
})
export class ArrowTabDirective implements OnDestroy {
    @ContentChildren(ArrowTabItemDirective)
    buttons: QueryList<ArrowTabItemDirective>;

    private _subscription: Subscription;

    constructor( keyPresses: KeyPressProvider) {
        this._subscription = keyPresses.subscribe( 'ArrowUp', 1, (event: KeyboardEvent) => {

            if ( event.repeat || event.type !== 'keydown') {
                return;
            }

            let index = -1;
            const activeButton = this.buttons.toArray().filter(v =>
                v.nativeElement === document.activeElement || v.nativeElement.contains(document.activeElement));
            if (activeButton && activeButton.length > 0) {
                index = this.buttons.toArray().indexOf(activeButton[0]);
            }

            let newIndex = index - 1;
            while (newIndex >= 0 && this.buttons.toArray()[newIndex].isDisabled()) {
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

            let index = -1;
            const activeButton = this.buttons.toArray().filter(v =>
                v.nativeElement === document.activeElement || v.nativeElement.contains(document.activeElement));
            if (activeButton && activeButton.length > 0) {
                index = this.buttons.toArray().indexOf(activeButton[0]);
            }

            let newIndex = index + 1;
            while (newIndex < this.buttons.length && this.buttons.toArray()[newIndex].isDisabled()) {
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
