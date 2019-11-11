import { Component, Output, EventEmitter, Injector } from '@angular/core';
import { OptionsListInterface } from './options-list.interface';
import { ScreenPart } from '../../../shared/decorators/screen-part.decorator';
import { ScreenPartComponent } from '../../../shared/screen-parts/screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { Observable } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';


@ScreenPart({
    name: 'optionsList'
})
@Component({
    selector: 'app-options-list',
    templateUrl: './options-list.component.html',
    styleUrls: ['./options-list.component.scss']
})
export class OptionsListComponent extends ScreenPartComponent<OptionsListInterface> {

    @Output()
    optionClick = new EventEmitter<IActionItem>();

    isMobile: Observable<boolean>;

    constructor(injector: Injector, mediaService: OpenposMediaService) {
        super(injector);
        this.isMobile = mediaService.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    screenDataUpdated() { }

    onOptionClick(actionItem: IActionItem): void {
        this.optionClick.emit(actionItem);
    }

}
