import {Component, Injector, Input} from '@angular/core';
import {ScreenPartComponent} from '../screen-part';
import {Reward, RewardsLineItemComponentInterface} from './rewards-line-item.interface';
import {Observable} from "rxjs";
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';


@Component({
    selector: 'app-rewards-line-item',
    templateUrl: './rewards-line-item.component.html',
    styleUrls: ['./rewards-line-item.component.scss']})
export class RewardsLineItemComponent extends ScreenPartComponent<RewardsLineItemComponentInterface>{
    @Input()
    reward: Reward;
    isMobile: Observable<boolean>;
    isFocused: boolean;
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

    setFocus(focused: boolean) { this.isFocused = !this.isFocused; }
}
