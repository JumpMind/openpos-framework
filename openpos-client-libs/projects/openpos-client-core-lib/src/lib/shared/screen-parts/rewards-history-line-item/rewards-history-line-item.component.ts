import {Component, Injector, Input} from '@angular/core';
import {ScreenPartComponent} from '../screen-part';
import { RewardHistory } from './rewards-history-line-item.interface';
import {Observable} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {RewardsHistoryLineItemComponentInterface} from './rewards-history-line-item.interface';

@Component({
    selector: 'app-rewards-history-line-item',
    templateUrl: './rewards-history-line-item.component.html',
    styleUrls: ['./rewards-history-line-item.component.scss']})
export class RewardsHistoryLineItemComponent extends ScreenPartComponent<RewardsHistoryLineItemComponentInterface>{
    @Input()
    reward: RewardHistory;
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
