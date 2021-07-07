import { Directive, ElementRef, Input, OnChanges, OnDestroy, OnInit, Renderer2, SimpleChanges } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { AnimationService } from '../../core/services/animation.service';
import { isObservable, merge, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';


@Directive({
    selector: '[appAnimation]'
})
export class AnimationDirective implements OnInit, OnChanges, OnDestroy {
    private readonly hostElement: Element;

    private destroyed$ = new Subject();
    private changed$ = new Subject();

    @Input('appAnimation') animationClass: string;
    @Input() restartWhen: Observable<any>;

    constructor(hostElementRef: ElementRef, private animationService: AnimationService, private renderer: Renderer2) {
        this.hostElement = hostElementRef.nativeElement;
    }

    ngOnInit() {
        this.renderer.addClass(this.hostElement, this.animationClass);
        this.listenForRestart();
    }

    ngOnChanges(changes: SimpleChanges) {
        this.changed$.next();
        this.listenForRestart();
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listenForRestart(): void {
        if (!isObservable(this.restartWhen)) {
            return;
        }

        this.restartWhen
            .pipe(
                takeUntil(merge(this.changed$, this.destroyed$))
            ).subscribe(() => this.restart());
    }

    restart(): void {
        this.animationService.restart(this.hostElement, this.animationClass);
    }
}