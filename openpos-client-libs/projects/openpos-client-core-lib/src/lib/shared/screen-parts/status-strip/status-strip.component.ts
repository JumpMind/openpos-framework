import { ScreenPartComponent } from '../screen-part';
import { StatusStripInterface } from './status-strip.interface';
import { MatDialog } from '@angular/material';
import { timer } from 'rxjs';
import { Configuration } from '../../../configuration/configuration';
import { SystemStatusType } from '../../../core/interfaces/system-status-type.enum';
import { Component } from '@angular/core';
import { MessageProvider } from '../../providers/message.provider';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { SystemStatusDialogComponent } from '../../components/system-status/system-status-dialog.component';
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

    constructor(protected dialog: MatDialog, messageProvider: MessageProvider) {
        super(messageProvider);
    }

    screenDataUpdated() {
        timer( 1000, 1000 ).subscribe( () => {
            if ( this.screenData.timestampBegin ) {
                this.timer = (Date.now() - this.screenData.timestampBegin) / 1000;
            }
            this.date = Date.now();
        });
    }

    showRegisterStatus(): boolean {
        if (this.screenData.systemStatus && Configuration.showRegisterStatus) {
            return Configuration.offlineOnlyRegisterStatus ?
                this.screenData.systemStatus.overallSystemStatus === SystemStatusType.Offline : true;
        } else {
            return false;
        }
    }

    onRegisterStatusClick(): void {
        if (Configuration.clickableRegisterStatus) {
            const dialogRef = this.dialog.open(SystemStatusDialogComponent, {
                width: '40%',
                data: {
                    devices: this.screenData.systemStatus.devices,
                    deviceHeader: 'Device/Database',
                    statusHeader: 'Status',
                    disableClose: false,
                    autoFocus: false
                }
            });

            dialogRef.afterClosed().subscribe(result => {});
        }
    }
}
