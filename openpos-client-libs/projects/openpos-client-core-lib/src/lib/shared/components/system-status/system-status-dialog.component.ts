import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { IDevice } from '../../../core/interfaces/device.interface';

@Component({
    selector: 'app-system-status-dialog',
    templateUrl: './system-status-dialog.component.html',
    styleUrls: ['./system-status.component.scss']
  })

  export class SystemStatusDialogComponent {
    devices: IDevice[];
    deviceHeader: string;
    statusHeader: string;

    constructor( public dialogRef: MatDialogRef<SystemStatusDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.devices = data.devices;
        this.statusHeader = data.statusHeader;
        this.deviceHeader = data.deviceHeader;
    }

    getOnlineString(device: IDevice): string {
        if (device.online) {
            return 'Online';
        }
        return 'Offline';
    }

    onClose() {
        this.dialogRef.close();
    }
}
