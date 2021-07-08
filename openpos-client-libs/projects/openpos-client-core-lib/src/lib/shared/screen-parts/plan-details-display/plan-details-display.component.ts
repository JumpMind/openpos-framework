import {Component, Injector, Input} from '@angular/core';
import {PlanDetailsDisplayComponentInterface} from './plan-details-display.interface';
import {ScreenPartComponent} from '../screen-part';
import {Plan} from "../../../screens-with-parts/sale/membership-details-dialog/membership-details-dialog.interface";
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";
import {Observable} from "rxjs";

@Component({
    selector: 'app-plan-details-display',
    templateUrl: './plan-details-display.component.html',
    styleUrls: ['./plan-details-display.component.scss']})
export class PlanDetailsDisplayComponent extends ScreenPartComponent<PlanDetailsDisplayComponentInterface> {
    @Input()
    plan: Plan;
    isMobile: Observable<boolean>;

    constructor(injector: Injector, private media: OpenposMediaService) {
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
    }
}
