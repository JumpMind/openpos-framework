import {Component, Injector, Input} from '@angular/core';
import {ScreenPartComponent} from '../screen-part';
import {Observable} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {MembershipPointsDisplayComponentInterface} from "./membership-points-display.interface";

@Component({
    selector: 'app-membership-points-display',
    templateUrl: './membership-points-display.component.html',
    styleUrls: ['./membership-points-display.component.scss']})
export class MembershipPointsDisplayComponent extends ScreenPartComponent<MembershipPointsDisplayComponentInterface>{
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
