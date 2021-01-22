import { OverlayContainer } from '@angular/cdk/overlay';
import { Directive, ElementRef, Renderer2 } from '@angular/core';
import { ModalOverlayContainer } from './modal-overlay-container';

@Directive({
    selector: '[appModalOverlayContainer]'
})
export class ModalOverlayContainerDirective {
    constructor(renderer: Renderer2, elementRef: ElementRef, container: OverlayContainer) {
        renderer.addClass(elementRef.nativeElement, 'cdk-overlay-container');

        (container as ModalOverlayContainer).setContainerElement(elementRef.nativeElement);
    }
}
