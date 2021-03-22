import {Component, Injector, Input} from '@angular/core';
import {ScreenPartComponent} from "../screen-part";
import {MutableListItemWithLabelComponentInterface} from "./mutable-list-item-with-label.interface";
import {Observable} from "rxjs/internal/Observable";
import {MediaBreakpoints, OpenposMediaService} from "../../../core/media/openpos-media.service";


@Component({
    selector: 'app-mutable-list-item-with-label',
    templateUrl: './mutable-list-item-with-label.component.html',
    styleUrls: ['./mutable-list-item-with-label.component.scss']})
export class MutableListItemWithLabelComponent extends ScreenPartComponent<MutableListItemWithLabelComponentInterface>{

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
