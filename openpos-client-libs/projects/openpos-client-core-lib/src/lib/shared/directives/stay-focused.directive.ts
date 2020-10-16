import {Directive, ElementRef, OnDestroy, OnInit} from '@angular/core';
import {merge, Subject} from 'rxjs';
import {debounceTime, filter, takeUntil, tap} from 'rxjs/operators';

@Directive({
    selector: '[appStayFocused]'
})
export class StayFocusedDirective implements OnInit, OnDestroy {
    contentMutated$ = new Subject();
    contentMutationObserver: MutationObserver;
    contentMutationOptions = {childList: true, subtree: true};

    destroyed$ = new Subject();
    disabled$ = new Subject();

    constructor(private elementRef: ElementRef) {
        this.contentMutationObserver = new MutationObserver(() => this.onContentsMutated());
    }

    ngOnInit(): void {
        this.enable();
    }

    ngOnDestroy(): void {
        this.disable();
    }

    enable(): void {
        // Stop any existing listeners in case this method is called multiple times
        this.disable();
        this.stayFocused();
    }

    disable(): void {
        this.destroyed$.next();
        this.contentMutationObserver.disconnect();
    }

    gotDistracted(): boolean {
        // This is "true" when no elements in the "body" have focus
        return document.activeElement === document.body;
    }

    hasAbilityToFocus(): boolean {
        return !!this.elementRef.nativeElement.focus;
    }

    focus(): void {
        console.log('[appStayFocused]: Changing focus from element', document.activeElement, 'to element', this.elementRef.nativeElement);
        this.elementRef.nativeElement.focus();
    }

    onContentsMutated(): void {
        console.log('[appStayFocused]: Host element contents changed');
        // An observable is used here because so rapidly firing events with can easily be throttled with:
        //
        // this.contentsMutated$.pipe(debounceTime(xxx)).subscribe(...)
        this.contentMutated$.next();
    }

    private stayFocused(): void {
        const stop$ = merge(this.disabled$, this.destroyed$);

        // ------------------------------------------------------------------------------------------------------------------
        // THE PROBLEM
        // ------------------------------------------------------------------------------------------------------------------
        // In some scenarios, after ngIf/ngFor modify the DOM, there is no currently focused element and the browser will
        // set the "body" element as the actively focused element "document.activeElement". When this happens, the
        // KeyPressSourceDirective [appKeypressSource] directive will no longer receive key press events. This is because
        // the KeyPressSourceDirective [appKeypressSource] is always on an element that's inside of the "body" element,
        // so it doesn't hear any events from it.
        //
        // ------------------------------------------------------------------------------------------------------------------
        // WHY THE OBVIOUS APPROACH USING FOCUS/BLUR EVENTS DID NOT WORK
        // ------------------------------------------------------------------------------------------------------------------
        // Listening for blur/focus events was problematic because the "body" is assigned focus between when an element is
        // blurred and another is focused. The problem here was, after an element was focused and then removed from the DOM,
        // another element doesn't receive focus so the "body" gets focused. Getting the timing right in order to detect this
        // was difficult and often produced inconsistent results.
        //
        // ------------------------------------------------------------------------------------------------------------------
        // THE FIX
        // ------------------------------------------------------------------------------------------------------------------
        // This fix works by detecting when there are no elements with focus (when document.activeElement === document.body)
        // and, when that happens, brings the focus back to the element this directive is attached to.
        //
        this.contentMutated$
            .pipe(
                // Wait for the event queue to empty before checking the currently focused element, like when using setTimeout(fn, 0)
                debounceTime(0),
                filter(() => this.gotDistracted()),
                tap(() => console.log('[appStayFocused]: Host element got distracted', this.elementRef.nativeElement)),
                filter(() => this.hasAbilityToFocus()),
                takeUntil(stop$)
            ).subscribe(() => this.focus());

        this.contentMutationObserver.observe(this.elementRef.nativeElement, this.contentMutationOptions);
    }
}
