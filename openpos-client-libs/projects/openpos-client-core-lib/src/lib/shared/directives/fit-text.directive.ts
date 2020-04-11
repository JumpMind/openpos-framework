import {
    AfterViewInit,
    Directive,
    ElementRef,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    Output,
    Renderer2,
    SimpleChanges
} from '@angular/core';
import {fromEvent, merge, of, Subject} from 'rxjs';
import {debounce, filter, takeUntil} from 'rxjs/operators';

@Directive({
    selector: '[fitText]'
})
export class FitTextDirective implements AfterViewInit, OnChanges, OnDestroy {
    @Input() maxFontSize = Infinity;
    @Input() minFontSize = -Infinity;
    @Input() debounce = 500;
    @Output() fitted = new EventEmitter<FitTextDirective>();

    contentWidth: number;
    availableWidth: number;
    currentFontSize: number;
    previousFontSize: number;

    contentMutationObserver: MutationObserver;
    contentChanged = false;

    fitTextCount = 0;
    destroyed$ = new Subject();

    // Update the font size for these DOM mutations
    mutations = {
        attributes: true,
        subtree: true,
        childList: true,
        characterData: true
    };

    constructor(private elementRef: ElementRef, private renderer: Renderer2) {
        this.contentMutationObserver = new MutationObserver(() => {
            this.contentChanged = true;
            this.fitText();
        });
    }

    ngAfterViewInit(): void {
        // Do sizing when various events happen
        this.updateOnWindowChanges();
        this.updateOnContentChanges();

        // Do initial sizing
        this.fitText();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.fitText();
    }

    ngOnDestroy(): void {
        this.destroyed$.next();
        this.stopUpdateOnContentChanges();
    }

    updateOnWindowChanges(): void {
        const resizeEvent = fromEvent(window, 'resize');
        const orientationChangeEvent = fromEvent(window, 'orientationchange');

        merge(resizeEvent, orientationChangeEvent)
            .pipe(
                // Using "debounce", instead of "debounceTime" prevents any need to make specific
                // updates in ngOnChanges because "debounce" will always use the current value of "this.debounce"
                debounce(() => of(this.debounce)),
                filter(() => this.needsRedrawn()),
                takeUntil(this.destroyed$)
            )
            .subscribe(() => this.fitText());
    }

    updateOnContentChanges(): void {
        this.contentMutationObserver.observe(this.elementRef.nativeElement, this.mutations);
        this.contentMutationObserver.observe(this.renderer.parentNode(this.elementRef.nativeElement), this.mutations);
    }

    stopUpdateOnContentChanges(): void {
        this.contentMutationObserver.disconnect();
    }

    redraw(): void {
        if (!this.needsRedrawn()) {
            console.log('[fitText]: No redraw necessary');
            return;
        }

        this.updateState();

        // Calculate the new font size using the ratio of available width to content width.
        // This isn't exact, so the "fit()" method will call this method until the max font size
        // is found. On average, it takes 2-3 tries.
        let newFontSize = (this.availableWidth / this.contentWidth) * this.currentFontSize;

        // Update the current font size, while keeping it within bounds of min/max values
        this.currentFontSize = Math.min(
            Math.max(this.minFontSize, newFontSize),
            this.maxFontSize
        );

        // This directive listens for attribute changes, so it's necessary to stop listening
        // while updating the style attribute to prevent infinite recursion.
        this.stopUpdateOnContentChanges();
        this.renderer.setStyle(this.elementRef.nativeElement, 'font-size', this.currentFontSize + 'px');
        // Now it's save to resume listening for DOM changes
        this.updateOnContentChanges();

        this.markAsClean();
    }

    updateState(): void {
        const element = this.elementRef.nativeElement;
        const elementStyle = getComputedStyle(element);

        // Track this element's and the parent's width
        this.availableWidth = this.computeAvailableWidth();
        this.contentWidth = element.getBoundingClientRect().width;

        // Track current and previous font sizes
        this.currentFontSize = this.currentFontSize || parseFloat(elementStyle.fontSize);
        this.previousFontSize = this.currentFontSize;
    }

    computeAvailableWidth(): number {
        const parent = this.renderer.parentNode(this.elementRef.nativeElement);
        const parentStyle = getComputedStyle(parent);
        const parentPadding = parseFloat(parentStyle.paddingLeft) + parseFloat(parentStyle.paddingRight);

        return parent.getBoundingClientRect().width - parentPadding;
    }

    markAsClean(): void {
        this.contentChanged = false;
    }

    fitText(): void {
        if (this.fitTextCount === 0) {
            console.log(`[fitText]: Fitting text "${this.elementRef.nativeElement.textContent}"`);
        }

        this.fitTextCount++;
        this.redraw();

        if (this.isFontSizeStable()) {
            console.log(`[fitText]: Fitted font size ${this.currentFontSize}px after ${this.fitTextCount} attempt(s)`);
            this.fitted.emit(this);
            this.fitTextCount = 0;
        } else {
            this.fitText();
        }
    }

    isFontSizeStable(): boolean {
        // Stability happens as the previous and current font size converge
        return Math.floor(this.previousFontSize) === Math.floor(this.currentFontSize);
    }

    didAvailableWidthChange(): boolean {
        // Check if the current parent width is different from the last saved value
        return this.availableWidth !== this.computeAvailableWidth();
    }

    needsRedrawn(): boolean {
        return this.contentChanged || this.didAvailableWidthChange() || !this.isFontSizeStable();
    }
}
