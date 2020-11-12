import { Directive, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, merge, Subject } from 'rxjs';
import { debounceTime, filter, first, takeUntil, tap } from 'rxjs/operators';
import { LockScreenService } from '../../core/lock-screen/lock-screen.service';
import { DialogService } from '../../core/services/dialog.service';

@Directive({
    selector: '[appStayFocused]'
})
export class StayFocusedDirective implements OnInit, OnDestroy {
    contentMutated$ = new Subject<MutationRecord[]>();
    contentMutationObserver: MutationObserver;
    contentMutationOptions = {childList: true, subtree: true};
    hostElement: HTMLElement;

    destroyed$ = new Subject();
    disabled$ = new Subject();
    stop$ = merge(this.disabled$, this.destroyed$);
    enabled$ = new BehaviorSubject(false);

    constructor(private elementRef: ElementRef, private lockScreenService: LockScreenService, private dialogService: DialogService) {
        this.hostElement = this.elementRef.nativeElement;
        this.contentMutationObserver = new MutationObserver((mutations: MutationRecord[]) => this.onContentsMutated(mutations));
    }

    ngOnInit(): void {
        this.enable();
    }

    ngOnDestroy(): void {
        this.disable();
        this.destroyed$.next();
    }

    enable(): void {
        if (this.enabled$.getValue()) {
            // Stop any existing listeners in case this method is called multiple times
            this.disable();
        }

        console.log('[appStayFocused]: Enabled');
        this.enabled$.next(true);
        this.stayFocused();
    }

    disable(): void {
        console.log('[appStayFocused]: Disabled');
        this.enabled$.next(false);
        this.disabled$.next();
        this.contentMutationObserver.disconnect();
    }

    gotDistracted(): boolean {
        // This is "true" when no elements in the "body" have focus
        return document.activeElement === document.body;
    }

    isDialogOpen(): boolean {
        return this.dialogService.isDialogOpen();
    }

    isLockScreenActive(): boolean {
        return this.lockScreenService.enabled$.getValue();
    }

    hasAbilityToFocus(): boolean {
        return !!this.hostElement.focus;
    }

    focus(): void {
        console.log('[appStayFocused]: Changing focus from element', document.activeElement, 'to element', this.hostElement);
        this.hostElement.focus();
    }

    shouldFocus(): boolean {
        return !this.isLockScreenActive()
            && !this.isDialogOpen()
            && this.gotDistracted()
            && this.hasAbilityToFocus();
    }

    private onContentsMutated(mutations: MutationRecord[]): void {
        // An observable is used here because rapidly firing events can easily be throttled with:
        //
        // this.contentsMutated$.pipe(debounceTime(xxx)).subscribe(...)
        this.contentMutated$.next(mutations);
    }

    private stayFocused(): void {
        console.log('[appStayFocused]: Listening for content changes in', document.body);
        this.stop$.pipe(first()).subscribe(() => console.log('[appStayFocused]: Stopped listening for content changes in', document.body));

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
                tap(mutations => console.log('[appStayFocused]: Contents changed', mutations, 'in', document.body)),
                filter(() => this.shouldFocus()),
                tap(() => console.log('[appStayFocused]: Host element got distracted', this.hostElement)),
                takeUntil(this.stop$)
            ).subscribe(() => this.focus());

        this.contentMutationObserver.observe(document.body, this.contentMutationOptions);
    }
}
