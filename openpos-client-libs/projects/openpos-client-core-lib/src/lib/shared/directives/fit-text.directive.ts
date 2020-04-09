import {
    AfterViewInit,
    Directive,
    ElementRef,
    Input,
    OnChanges,
    OnDestroy,
    Renderer2,
    SimpleChanges
} from '@angular/core';
import {fromEvent, merge, of, Subject} from 'rxjs';
import {debounce, takeUntil, tap} from 'rxjs/operators';

@Directive({
    selector: '[fitText]'
})
export class FitTextDirective implements AfterViewInit, OnChanges, OnDestroy {
    @Input() maxFontSize = Infinity;
    @Input() minFontSize = -Infinity;
    @Input() debounce = 250;

    currentFontSize: number;
    previousFontSize: number;

    currentWidth: number;
    availableWidth: number;

    contentMutationObserver: MutationObserver;
    animationFrameRequestId: number;
    needsRedrawn = false;
    destroyed$ = new Subject();

    constructor(private elementRef: ElementRef, private renderer: Renderer2) {

    }

    ngAfterViewInit(): void {
        this.updateOnWindowChanges();
        this.updateOnContentChanges();
        this.updateUntilStable();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.updateUntilStable();
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
                debounce(() => of(this.debounce)),
                tap(() => this.needsRedrawn = true),
                takeUntil(this.destroyed$)
            )
            .subscribe(() => this.updateUntilStable());
    }

    updateOnContentChanges(): void {
        const mutations = {
            subtree: true,
            childList: true,
            characterData: true
        };

        this.contentMutationObserver = new MutationObserver(() => {
            this.needsRedrawn = true;
            this.updateUntilStable()
        });
        this.contentMutationObserver.observe(this.elementRef.nativeElement, mutations);
    }

    update(): void {
        if (this.isStable() && !this.needsRedrawn) {
            return;
        }

        const element = this.elementRef.nativeElement;
        const parent = this.renderer.parentNode(element);
        const style = getComputedStyle(element);

        this.availableWidth = parent.clientWidth;
        this.currentWidth = element.getBoundingClientRect().width;

        this.currentFontSize = this.currentFontSize || parseFloat(style.fontSize);
        this.previousFontSize = this.currentFontSize;

        const newFontSize = (this.availableWidth / this.currentWidth) * this.currentFontSize;

        this.currentFontSize = Math.min(
            Math.max(this.minFontSize, newFontSize),
            this.maxFontSize
        );

        this.renderer.setStyle(element, 'font-size', this.currentFontSize + 'px');
    }

    updateUntilStable(): void {
        cancelAnimationFrame(this.animationFrameRequestId);

        this.animationFrameRequestId = requestAnimationFrame(() => {
            this.update();

            if (!this.isStable()) {
                this.updateUntilStable();
            }
        });
    }

    isStable(): boolean {
        return Math.floor(this.previousFontSize) === Math.floor(this.currentFontSize);
    }
}
