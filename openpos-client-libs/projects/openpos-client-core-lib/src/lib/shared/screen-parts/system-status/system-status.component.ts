import { ISystemStatus } from '../../../core/interfaces/system-status.interface';
import { ScreenPartComponent } from '../screen-part';
import { Component, Injector } from '@angular/core';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Configuration } from '../../../configuration/configuration';
import { SystemStatusType } from '../../../core/interfaces/system-status-type.enum';
import { SystemStatusDialogComponent } from '../../components/system-status/system-status-dialog.component';
import { MatDialog } from '@angular/material';

@ScreenPart({
    name: 'statusStrip.systemStatus'
})
@Component({
    selector: 'app-system-status',
    templateUrl: './system-status.component.html',
    styleUrls: ['./system-status.component.scss'],
})
export class SystemStatusComponent extends ScreenPartComponent<ISystemStatus> {

    constructor(protected dialog: MatDialog, injector: Injector) {
        super(injector);
    }

    screenDataUpdated() {
    }

    showRegisterStatus(): boolean {
        if (this.screenData && Configuration.showRegisterStatus) {
            return Configuration.offlineOnlyRegisterStatus ?
                this.screenData.overallSystemStatus === SystemStatusType.Offline : true;
        } else {
            return false;
        }
    }

    onRegisterStatusClick(): void {
        if (Configuration.clickableRegisterStatus) {
            const dialogRef = this.dialog.open(SystemStatusDialogComponent, {
                width: '40%',
                data: {
                    devices: this.screenData.devices,
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
