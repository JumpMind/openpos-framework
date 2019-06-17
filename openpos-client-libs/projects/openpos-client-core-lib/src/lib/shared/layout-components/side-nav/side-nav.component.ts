import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';

@Component({
    selector: 'app-side-nav',
    templateUrl: './side-nav.component.html',
    styleUrls: ['./side-nav.component.scss']
  })
  export class SideNavComponent {

    drawerOpen$: Observable<boolean>;
    drawerMode$: Observable<string>;
    isMobile$: Observable<boolean>;

    constructor( private mediaService: OpenposMediaService ) {
        const openMap = new Map([
            ['xs', false],
            ['sm', true],
            ['md', true],
            ['lg', true],
            ['xl', true]
        ]);

        const modeMap = new Map([
            ['xs', 'over'],
            ['sm', 'side'],
            ['md', 'side'],
            ['lg', 'side'],
            ['xl', 'side']
          ]);
          const isMobile = new Map([
            ['xs', true],
            ['sm', false],
            ['md', false],
            ['lg', false],
            ['xl', false]
        ]);
        this.isMobile$ = mediaService.mediaObservableFromMap(isMobile);
        this.drawerOpen$ = mediaService.mediaObservableFromMap(openMap);
        this.drawerMode$ = mediaService.mediaObservableFromMap(modeMap);
    }
}
