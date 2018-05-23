import { Directive, HostListener, Input, Output, EventEmitter } from '@angular/core';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[inactivityMonitor]'
})
export class InactivityMonitorDirective {

    static lastKeepAliveFlushTime: number = new Date().getTime();

    constructor() {
    }

    @Input() keepAliveMillis = 30000;

    @Output() issueKeepAlive: EventEmitter<string> = new EventEmitter<string>();

    private timerHandle: any;
    private _timeoutActive;

    @HostListener('window:keydown', ['$event'])
    keyEvent(event: KeyboardEvent) {
        this.keepAlive();
    }

    @HostListener('window:click', ['$event'])
    mouseEvent(event: MouseEvent) {
        this.keepAlive();
    }

    @HostListener('window:touchstart', ['$event'])
    touchEvent(event: TouchEvent) {
        this.keepAlive();
    }

    private keepAlive() {
        const now = new Date().getTime();
        const nextFlushTime = InactivityMonitorDirective.lastKeepAliveFlushTime + this.keepAliveMillis;
        if (now > nextFlushTime) {
            InactivityMonitorDirective.lastKeepAliveFlushTime = now;
            this.issueKeepAlive.emit('KeepAlive');
        }
    }
}
