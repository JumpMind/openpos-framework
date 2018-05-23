import { Directive, Input, ElementRef, HostListener } from '@angular/core';
import { DeviceService } from '../services';

@Directive({
    selector: '[autoSelectOnFocus]'
})
export class AutoSelectOnFocus {

    private element: ElementRef;
    private touchEvent: boolean;

    constructor(el: ElementRef, private deviceService: DeviceService) {
        this.element = el;
    }

    @HostListener('touchend', ['$event'])
    onTouchEnd($event: Event) {
        // console.log('autoSelect: Got touchend event');
        this.touchEvent = true;
    }


    @HostListener('focus', ['$event'])
    onFocus($event: Event) {

        if (this.deviceService.isRunningInCordova()) {
            // console.log('autoSelect: Got focus event');
            // There is not enough information on the event object to know
            // if the focus event came from a touch event or keyboard event.  On iOS
            // with Cordova if the event was NOT a touch event, then do the selection.
            // This will occur when user uses iOS keyboard controls to move between
            // fields
            if (! this.touchEvent) {
                // console.log('autoSelect: cordova, not touch event, selectingRange');
                this.element.nativeElement.setSelectionRange(0, 9999);
            }
        } else {
            // console.log('autoSelect: browser, selectingRange');
            this.element.nativeElement.setSelectionRange(0, 9999);
        }
        this.touchEvent = false;
    }

    @HostListener('click', ['$event'])
    onClick($event: Event) {
        // When running on iPad with Cordova, onClick is only thing that seems to really work
        // for selecting the field.
        if (this.deviceService.isRunningInCordova()) {
            // console.log('autoSelect: cordova click event, selectingRange');
            this.element.nativeElement.setSelectionRange(0, 9999);
        }
    }

}
