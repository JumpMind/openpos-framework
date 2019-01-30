import { Component, Input, ViewChild, OnInit } from '@angular/core';
import { OpenposMediaService } from '../../../core';
import { Observable } from 'rxjs';
import { MatSidenav } from '@angular/material';

@Component({
    selector: 'app-waffle',
    templateUrl: './waffle.component.html',
    styleUrls: ['./waffle.component.scss']
  })
  export class WaffleComponent {

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
