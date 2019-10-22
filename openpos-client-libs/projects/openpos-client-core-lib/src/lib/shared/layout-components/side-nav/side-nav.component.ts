import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { OpenposMediaService, MediaBreakpoints } from '../../../core/media/openpos-media.service';

@Component({
    selector: 'app-side-nav',
    templateUrl: './side-nav.component.html',
    styleUrls: ['./side-nav.component.scss']
})
export class SideNavComponent {

    drawerOpen$: Observable<boolean>;
    drawerMode$: Observable<string>;
    isMobile$: Observable<boolean>;

    constructor(private mediaService: OpenposMediaService) {
        const openMap = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, false],
            [MediaBreakpoints.MOBILE_LANDSCAPE, false],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, true],
            [MediaBreakpoints.DESKTOP_PORTRAIT, true],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, true]
        ]);

        const modeMap = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, 'over'],
            [MediaBreakpoints.MOBILE_LANDSCAPE, 'over'],
            [MediaBreakpoints.TABLET_PORTRAIT, 'side'],
            [MediaBreakpoints.TABLET_LANDSCAPE, 'side'],
            [MediaBreakpoints.DESKTOP_PORTRAIT, 'side'],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, 'side']
        ]);
        const isMobile = new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, false],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]);
        this.isMobile$ = mediaService.observe(isMobile);
        this.drawerOpen$ = mediaService.observe(openMap);
        this.drawerMode$ = mediaService.observe(modeMap);
    }
}
