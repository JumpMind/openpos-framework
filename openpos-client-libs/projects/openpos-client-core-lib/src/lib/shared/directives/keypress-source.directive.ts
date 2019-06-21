import { Directive, ElementRef, OnInit, HostListener, Renderer2 } from '@angular/core';
import { KeyPressProvider } from '../providers/keypress.provider';
import { Subject } from 'rxjs';

@Directive({
    selector: '[appKeypressSource]',
    providers: [KeyPressProvider]
})
export class KeyPressSourceDirective implements OnInit {

    keydown$ = new Subject<KeyboardEvent>();
    keyup$ = new Subject<KeyboardEvent>();

    constructor( private renderer: Renderer2, private el: ElementRef, private keyPressProvider: KeyPressProvider) {
    }

    ngOnInit(): void {
        this.keyPressProvider.registerKeyPressSource(this.keyup$);
        this.keyPressProvider.registerKeyPressSource(this.keydown$);
        // Need to do this so that this element can grab key events
        this.renderer.setAttribute(this.el.nativeElement, 'tabindex', '0');
    }


    @HostListener('keydown', ['$event'])
    onkeydown( event: KeyboardEvent) {
        this.keydown$.next(event);
    }

    @HostListener('keyup', ['$event'])
    onkeyup( event: KeyboardEvent) {
        this.keyup$.next(event);
    }
}
