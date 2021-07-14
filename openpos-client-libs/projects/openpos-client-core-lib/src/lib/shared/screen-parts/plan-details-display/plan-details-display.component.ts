import {Component, Injector, Input} from '@angular/core';
import {PlanDetailsDisplayComponentInterface} from './plan-details-display.interface';
import {ScreenPartComponent} from '../screen-part';
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";
import {Observable} from "rxjs";
import {Plan} from "../../../screens-with-parts/sale/program-interface";
import {SafeHtml} from "@angular/platform-browser/src/security/dom_sanitization_service";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
    selector: 'app-plan-details-display',
    templateUrl: './plan-details-display.component.html',
    styleUrls: ['./plan-details-display.component.scss']})
export class PlanDetailsDisplayComponent extends ScreenPartComponent<PlanDetailsDisplayComponentInterface> {
    @Input()
    plan: Plan;
    safeCopy: SafeHtml;
    isMobile: Observable<boolean>;

    constructor(injector: Injector, private media: OpenposMediaService, private sanitizer: DomSanitizer) {
        super(injector);
        this.initIsMobile();
    }

    initIsMobile(): void {
        this.isMobile = this.media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    screenDataUpdated() {
        this.safeCopy =  this.sanitizer.bypassSecurityTrustHtml(this.plan.copy);
    }
}
