import {Component, Injector} from '@angular/core';
import { SaleTotalPanelInterface } from './sale-total-panel.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Configuration } from '../../../configuration/configuration';
import {Observable} from "rxjs";
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";


@ScreenPart({
    name: 'SaleTotalPanel'
})
@Component({
    selector: 'app-sale-total-panel',
    templateUrl: './sale-total-panel.component.html',
    styleUrls: ['./sale-total-panel.component.scss']
})
export class SaleTotalPanelComponent extends ScreenPartComponent<SaleTotalPanelInterface> {
    isMobile: Observable<boolean>;
    constructor(injector: Injector, media: OpenposMediaService) {
        super(injector);
        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }
    screenDataUpdated() {}

    public keybindsEnabled(menuItem: IActionItem): boolean {
        return Configuration.enableKeybinds && !!menuItem.keybind && menuItem.keybind !== 'Enter';
    }

    public doMenuItemAction(menuItem: IActionItem) {
        this.doAction(menuItem);
    }

}
