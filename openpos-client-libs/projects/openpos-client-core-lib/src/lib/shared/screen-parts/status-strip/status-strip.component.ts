import {map} from 'rxjs/operators';
import { ScreenPartComponent } from '../screen-part';
import { StatusStripInterface } from './status-strip.interface';
import { MatDialog } from '@angular/material';
import {interval, of, timer} from 'rxjs';
import { Component, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
@ScreenPart({
    name: 'statusStrip'
})
@Component({
    selector: 'app-status-strip',
    templateUrl: './status-strip.component.html',
    styleUrls: ['./status-strip.component.scss'],
})
export class StatusStripComponent extends ScreenPartComponent<StatusStripInterface> {

    date = interval(1000).pipe( map( () => Date.now()));
    timer = interval(1000).pipe(map( () => {
        if ( this.screenData.timestampBegin ) {
        const timestampBegin = this.screenData.timestampBegin;
        return ((new Date()).getTime() - timestampBegin) / 1000;
        } 
    }));

    constructor(protected dialog: MatDialog, injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
    }
}
