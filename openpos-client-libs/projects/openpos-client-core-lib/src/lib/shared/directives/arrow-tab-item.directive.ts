import { Directive, ElementRef, Input } from '@angular/core';

@Directive({
    selector: '[appArrowTabItem]'
})
export class ArrowTabItemDirective {

    @Input()
    disabled = false;

    constructor(private elementRef: ElementRef) {
    }

    isDisabled() {
        return (this.disabled || this.elementRef.nativeElement.disabled);
    }

    get nativeElement() {
        return this.elementRef.nativeElement;
    }
}
