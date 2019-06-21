import { Directive, HostListener, Renderer2, ElementRef, OnDestroy } from '@angular/core';
import 'hammerjs';
import 'hammer-timejs';
import { Subscription } from 'rxjs';
import { SessionService } from '../../core/services/session.service';
import { Configuration } from '../../configuration/configuration';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[mat-card-content], [scrollable]'
})
export class MimicScrollDirective implements OnDestroy {

    private screenSubscription: Subscription;
    private screen: any;
    private unlistenMethod = () => { };

    constructor(private elRef: ElementRef, public session: SessionService, public renderer: Renderer2) {
        if (Configuration.mimicScroll) {
            this.unlistenMethod = this.renderer.listen(elRef.nativeElement, 'panmove', (event) => {
                this.elRef.nativeElement.scrollTop -= event.deltaY / 10;
            });
        }
    }

    ngOnDestroy(): void {
        this.unlistenMethod();
    }

}
