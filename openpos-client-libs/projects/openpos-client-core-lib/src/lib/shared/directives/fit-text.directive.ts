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
    @Input() debounce = 250;
    @Output() fitted = new EventEmitter<FitTextDirective>();

    currentFontSize: number;
    previousFontSize: number;

    contentWidth: number;
    availableWidth: number;

    contentMutationObserver: MutationObserver;
    contentChanged = false;

    animationFrameRequestId: number;
    destroyed$ = new Subject();

    constructor(private elementRef: ElementRef, private renderer: Renderer2) {
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

        if (this.contentMutationObserver) {
            this.contentMutationObserver.disconnect();
        }
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
        // Update the font size for these DOM mutations
        const mutations = {
            subtree: true,
            childList: true,
            characterData: true
        };

        this.contentMutationObserver = new MutationObserver(() => {
            this.contentChanged = true;
            this.fitText();
        });
        this.contentMutationObserver.observe(this.elementRef.nativeElement, mutations);
    }

    redraw(): void {
        if (!this.needsRedrawn()) {
            return;
        }

        const element = this.elementRef.nativeElement;
        const parent = this.renderer.parentNode(element);
        const style = getComputedStyle(element);

        this.availableWidth = parent.clientWidth;
        this.contentWidth = element.getBoundingClientRect().width;

        this.currentFontSize = this.currentFontSize || parseFloat(style.fontSize);
        this.previousFontSize = this.currentFontSize;

        let newFontSize = (this.availableWidth / this.contentWidth) * this.currentFontSize;

        // Update the current font size, while keeping it within bounds of min/max values
        this.currentFontSize = Math.min(
            Math.max(this.minFontSize, newFontSize),
            this.maxFontSize
        );

        this.renderer.setStyle(element, 'font-size', this.currentFontSize + 'px');
        this.markAsClean();
    }

    markAsClean(): void {
        this.contentChanged = false;
    }

    fitText(): void {
        cancelAnimationFrame(this.animationFrameRequestId);

        // Update the font size the next time the browser is ready to start the next repaint task
        this.animationFrameRequestId = requestAnimationFrame(() => {
            this.redraw();

            if (this.isFontSizeStable()) {
                this.fitted.emit(this);
            } else {
                this.fitText();
            }
        });
    }

    isFontSizeStable(): boolean {
        // Stability happens as the previous and current font size converge
        return Math.floor(this.previousFontSize) === Math.floor(this.currentFontSize);
    }

    didAvailableWidthChange(): boolean {
        // Check if the current parent width is different from the last saved value
        const parent = this.renderer.parentNode(this.elementRef.nativeElement);
        return this.availableWidth !== parent.clientWidth;
    }

    needsRedrawn(): boolean {
        return this.contentChanged || this.didAvailableWidthChange() || !this.isFontSizeStable();
    }
}
