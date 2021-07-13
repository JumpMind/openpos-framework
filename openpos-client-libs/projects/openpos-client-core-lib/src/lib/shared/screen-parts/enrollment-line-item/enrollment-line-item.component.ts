import {Component, Injector, Input} from '@angular/core';
import {ScreenPartComponent} from '../screen-part';
import {EnrollmentLineItemComponentInterface} from './enrollment-line-item.interface';
import {Observable} from "rxjs";
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {EnrollmentItem} from "../../../screens-with-parts/sale/program-interface";

@Component({
    selector: 'app-enrollment-line-item',
    templateUrl: './enrollment-line-item.component.html',
    styleUrls: ['./enrollment-line-item.component.scss']})
export class EnrollmentLineItemComponent extends ScreenPartComponent<EnrollmentLineItemComponentInterface>{
    @Input()
    enrollment: EnrollmentItem;
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
