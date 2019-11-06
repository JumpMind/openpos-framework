import { MatSidenav } from '@angular/material/sidenav';
import { BaconStripInterface } from './bacon-strip.interface';
import { ScreenPartComponent } from '../screen-part';
import { Component, ViewChild, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { HelpTextService } from '../../../core/help-text/help-text.service';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';
import { Observable } from 'rxjs';

@ScreenPart({
    name: 'baconStrip'
})
@Component({
    selector: 'app-bacon-strip',
    templateUrl: './bacon-strip.component.html',
    styleUrls: ['./bacon-strip.component.scss']
})
export class BaconStripComponent extends ScreenPartComponent<BaconStripInterface> {

    operatorInfo: string;
    iconButtonName: string;

    @ViewChild(MatSidenav)
    baconDrawer: MatSidenav;

    isMobile: Observable<boolean>;

    searchExpanded = false;
    constructor(injector: Injector, public helpTextService: HelpTextService, private media: OpenposMediaService) {
        super(injector);

        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, false],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    screenDataUpdated() {
        if (this.screenData.actions && this.screenData.actions.length == 1) {
            this.iconButtonName = this.screenData.actions[0].icon;
        } else if (this.screenData.actions) {
            this.iconButtonName = 'menu';
        }

        if (this.screenData.operatorText && this.screenData.deviceId) {
            this.operatorInfo = this.screenData.operatorText + ' on ' + this.screenData.deviceId;
        } else {
            this.operatorInfo = this.screenData.operatorText ? this.screenData.operatorText : this.screenData.deviceId;
        }
    }

    buttonClick() {
        if (this.screenData.actions.length == 1) {
            this.doAction(this.screenData.actions[0]);
        } else {
            this.baconDrawer.toggle();
        }
    }

    onSearchExpand(expanded: boolean): void {
        this.searchExpanded = expanded;
    }
}
