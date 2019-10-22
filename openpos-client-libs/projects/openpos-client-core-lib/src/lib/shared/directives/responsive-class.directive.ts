import { Directive, ElementRef, OnDestroy, Renderer2 } from '@angular/core';
import { Subscription } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../core/services/openpos-media.service';

@Directive({
    selector: '[responsive-class]'
})
export class ResponsiveClassDirective implements OnDestroy {

    readonly subscription: Subscription;

    private nameToClasses = new Map([
        [MediaBreakpoints.MOBILE_PORTRAIT, ['mobile', 'mobile-portrait']],
        [MediaBreakpoints.TABLET_PORTRAIT, ['tablet', 'tablet-portrait']],
        [MediaBreakpoints.DESKTOP_PORTRAIT, ['desktop', 'desktop-portrait']],
        [MediaBreakpoints.MOBILE_LANDSCAPE, ['mobile', 'mobile-landscape']],
        [MediaBreakpoints.TABLET_LANDSCAPE, ['tablet', 'tablet-landscape']],
        [MediaBreakpoints.DESKTOP_LANDSCAPE, ['desktop', 'desktop-landscape']]
    ]);

    private breakpointClasses = ['mobile', 'tablet', 'desktop',
        'mobile-portrait', 'tablet-portrait', 'desktop-portrait',
        'mobile-landscape', 'tablet-landscape', 'desktop-landscape'];

    constructor(
        mediaService: OpenposMediaService,
        renderer: Renderer2,
        elRef: ElementRef
    ) {
        this.subscription = mediaService.observe(this.nameToClasses).subscribe(classNames => {

            this.breakpointClasses.forEach(className => {
                renderer.removeClass(elRef.nativeElement, className);
            });

            classNames.forEach(classes => {
                classes.forEach(className => {
                    renderer.addClass(elRef.nativeElement, className);
                });
            });
        });
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

}
