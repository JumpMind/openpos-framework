import { Component, Input } from '@angular/core';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-overflow-list',
    templateUrl: './overflow-list.component.html',
    styleUrls: ['./overflow-list.component.scss']
})
export class OverFlowListComponent {

    @Input() numberItemsToShow: Observable<number> = this.mediaService.observe(new Map([
        [MediaBreakpoints.MOBILE_PORTRAIT, 3],
        [MediaBreakpoints.MOBILE_LANDSCAPE, 3],
        [MediaBreakpoints.TABLET_PORTRAIT, 4],
        [MediaBreakpoints.TABLET_LANDSCAPE, 4],
        [MediaBreakpoints.DESKTOP_PORTRAIT, 6],
        [MediaBreakpoints.DESKTOP_LANDSCAPE, 6]
    ]));

    @Input() items: any[];

    shownItems: any[];
    overflowItems: any[];

    constructor(private mediaService: OpenposMediaService) { }
}
