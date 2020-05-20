import {Directive, ElementRef, OnDestroy, OnInit, Renderer2} from '@angular/core';
import {KeyPressProvider} from '../providers/keypress.provider';
import {fromEvent, merge, Subject} from 'rxjs';
import {filter, takeUntil, tap} from 'rxjs/operators';

@Directive({
    selector: '[appKeypressSource]',
    providers: [KeyPressProvider]
})
export class KeyPressSourceDirective implements OnInit, OnDestroy {
    keydown$ = new Subject<KeyboardEvent>();
    keyup$ = new Subject<KeyboardEvent>();
    destroyed$ = new Subject();

    constructor(private renderer: Renderer2, private el: ElementRef, private keyPressProvider: KeyPressProvider) {
    }

    ngOnInit(): void {
        this.keyPressProvider.registerKeyPressSource(this.keyup$);
        this.keyPressProvider.registerKeyPressSource(this.keydown$);
        // Need to do this so that this element can grab key events
        this.renderer.setAttribute(this.el.nativeElement, 'tabindex', '0');

        const keydownEvent$ = fromEvent<KeyboardEvent>(this.el.nativeElement, 'keydown');
        const keyupEvent$ = fromEvent<KeyboardEvent>(this.el.nativeElement, 'keyup');

        merge(keydownEvent$, keyupEvent$).pipe(
            filter(event => this.el.nativeElement.contains(event.target)),
            tap(event => this.handleKeyboardEvent(event)),
            takeUntil(this.destroyed$)
        ).subscribe();
    }

    handleKeyboardEvent(event: KeyboardEvent): void {
        if(event.type === 'keydown') {
            this.keydown$.next(event)
        } else {
            this.keyup$.next(event)
        }

        if(this.keyPressProvider.keyHasSubscribers(event)) {
            event.stopPropagation();
            event.preventDefault();
            const key = this.keyPressProvider.getNormalizedKey(event);
            console.log(`[appKeypressSource]: Handling "${event.type}" event for "${key}" for element`, this.el.nativeElement);
        }
    }

    ngOnDestroy(): void {
        this.keyPressProvider.unregisterKeyPressSource(this.keyup$);
        this.keyPressProvider.unregisterKeyPressSource(this.keydown$);
        this.destroyed$.next();
    }
}
