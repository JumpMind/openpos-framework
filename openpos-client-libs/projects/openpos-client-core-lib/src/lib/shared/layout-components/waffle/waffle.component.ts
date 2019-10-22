import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';

@Component({
    selector: 'app-waffle',
    templateUrl: './waffle.component.html',
    styleUrls: ['./waffle.component.scss']
})
export class WaffleComponent {

    @Input()
    showDrawer = true;

    public drawerOpen: Observable<boolean>;
    public drawerMode: Observable<string>;

    constructor(private mediaService: OpenposMediaService) {
        const openMap = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, false],
            [MediaBreakpoints.MOBILE_LANDSCAPE, false],
            [MediaBreakpoints.TABLET_PORTRAIT, false],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, true],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, true]
        ]);

        const modeMap = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, 'over'],
            [MediaBreakpoints.MOBILE_LANDSCAPE, 'over'],
            [MediaBreakpoints.TABLET_PORTRAIT, 'over'],
            [MediaBreakpoints.TABLET_LANDSCAPE, 'side'],
            [MediaBreakpoints.DESKTOP_PORTRAIT, 'side'],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, 'side']
        ]);

        this.drawerOpen = this.mediaService.observe(openMap);
        this.drawerMode = this.mediaService.observe(modeMap);
    }

}
