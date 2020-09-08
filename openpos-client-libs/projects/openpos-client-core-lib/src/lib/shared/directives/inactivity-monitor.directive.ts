import { Directive, HostListener, Input, Renderer2, ElementRef, OnDestroy } from '@angular/core';
import { Configuration } from '../../configuration/configuration';
import { SessionService } from '../../core/services/session.service';
import {fromEvent, merge, Subject} from 'rxjs';
import {takeUntil, throttleTime} from 'rxjs/operators';


@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[inactivityMonitor]'
})
export class InactivityMonitorDirective implements OnDestroy {
    private destroyed$ = new Subject();
    static lastKeepAliveFlushTime: number = new Date().getTime();
    private eventThrottleTime = 500;

    @Input() keepAliveMillis = Configuration.keepAliveMillis;

    constructor(private elRef: ElementRef, public renderer: Renderer2, private session: SessionService) {
        if (Configuration.useTouchListener) {
            fromEvent(elRef.nativeElement, 'touchstart')
                .pipe(takeUntil(this.destroyed$)).subscribe((event: TouchEvent) => this.touchEvent(event));
        }

        const throttledEvents = merge(
            // Scroll events don't bubble, so we need to get them top-down to handle them globally
            fromEvent(window, 'scroll', {capture: true}),
            // Capture move move, instead of bubble, so we'll still be notified if nested components stop propagation of event bubbling
            fromEvent(window,'mousemove', {capture: true})
        );

        throttledEvents.pipe(
                // leading: get notified at start of throttle interval (as opposed to debounceTime, which waits xxx ms after first event)
                // trailing: get notified at end of throttle interval too
                throttleTime(this.eventThrottleTime, undefined, { leading: true, trailing: true }),
                takeUntil(this.destroyed$)
            ).subscribe(event => this.throttledEvent(event));
    }

    ngOnDestroy(): void {
        this.destroyed$.next();
    }


    @HostListener('window:keydown', ['$event'])
    keyEvent(event: KeyboardEvent) {
        this.keepAlive();
    }

    @HostListener('window:click', ['$event'])
    mouseClickEvent(event: MouseEvent) {
        this.keepAlive();
    }

    touchEvent(event: TouchEvent) {
        this.keepAlive();
    }

    throttledEvent(event: Event) {
        this.keepAlive();
    }

    private keepAlive() {
        const now = new Date().getTime();
        const nextFlushTime = InactivityMonitorDirective.lastKeepAliveFlushTime + this.keepAliveMillis;
        if (now > nextFlushTime) {
            InactivityMonitorDirective.lastKeepAliveFlushTime = now;
            this.session.keepAlive();
        }
    }
}
