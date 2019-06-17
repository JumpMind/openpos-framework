import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { OpenposMediaService } from '../../../core/services/openpos-media.service';

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

        this.drawerOpen = this.mediaService.mediaObservableFromMap(openMap);
        this.drawerMode = this.mediaService.mediaObservableFromMap(modeMap);
    }

}
