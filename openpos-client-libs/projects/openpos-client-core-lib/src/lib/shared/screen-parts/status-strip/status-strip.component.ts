import { ScreenPartComponent } from '../screen-part';
import { StatusStripInterface } from './status-strip.interface';
import { MatDialog } from '@angular/material';
import { timer } from 'rxjs';
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

    date = Date.now();
    timer: number;

    constructor(protected dialog: MatDialog, injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
        timer( 1000, 1000 ).subscribe( () => {
            if ( this.screenData.timestampBegin ) {
                this.timer = (Date.now() - this.screenData.timestampBegin) / 1000;
            }
            this.date = Date.now();
        });
    }
}
