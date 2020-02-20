import { MatSidenav } from '@angular/material/sidenav';
import { BaconStripInterface } from './bacon-strip.interface';
import { ScreenPartComponent } from '../screen-part';
import { Component, ViewChild, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { HelpTextService } from '../../../core/help-text/help-text.service';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';
import { Observable } from 'rxjs';
import { KeyPressProvider } from '../../providers/keypress.provider';
import { Configuration } from '../../../configuration/configuration';

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
    statusLine;
    iconButtonName: string;

    @ViewChild(MatSidenav)
    baconDrawer: MatSidenav;

    isMobile: Observable<boolean>;

    searchExpanded = false;
    constructor(injector: Injector, public helpTextService: HelpTextService, private media: OpenposMediaService,
                protected keyPresses: KeyPressProvider) {
        super(injector);

        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));

        this.subscriptions.add(
          this.keyPresses.subscribe( 'Escape', 1, (event: KeyboardEvent) => {
            // ignore repeats and check configuration
            if ( event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) {
              return;
            }
            if ( event.type === 'keydown' && this.screenData.actions) {
              this.buttonClick();
            }
          })
        );
    }

    screenDataUpdated() {
        if (this.screenData.actions && this.screenData.actions.length === 1) {
            this.iconButtonName = this.screenData.actions[0].icon;
        } else if (this.screenData.actions) {
            this.iconButtonName = 'menu';
        }

        if (this.screenData.operatorText && this.screenData.deviceId) {
            this.operatorInfo = this.screenData.operatorText + ' on ' + this.screenData.deviceId;
        } else {
            this.operatorInfo = this.screenData.operatorText ? this.screenData.operatorText : this.screenData.deviceId;
        }

        this.statusLine = '';
        if (this.screenData.certification) {
            this.statusLine += 'CC: ' + this.screenData.certification;
        }
        if (this.screenData.version) {
            this.statusLine += ' Version: ' + this.screenData.version;
        }
    }

    buttonClick() {
        if (this.screenData.actions.length === 1) {
            this.doAction(this.screenData.actions[0]);
        } else {
            this.baconDrawer.toggle();
        }
    }

    onSearchExpand(expanded: boolean): void {
        this.searchExpanded = expanded;
    }
}
