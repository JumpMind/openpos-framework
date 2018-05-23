import { Directive, Input, ElementRef, AfterContentInit } from '@angular/core';

@Directive({
    selector: '[appFocus]'
})
export class FocusDirective implements AfterContentInit {

    @Input()
    appFocus: boolean;

    constructor(private element: ElementRef) {
    }

    ngAfterContentInit(): void {
        if (this.appFocus) {
            this.element.nativeElement.focus();
        }
    }
}
