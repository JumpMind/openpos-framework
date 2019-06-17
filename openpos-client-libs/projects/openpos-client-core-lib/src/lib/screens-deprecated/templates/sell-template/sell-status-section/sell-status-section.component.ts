import { Component, Input, OnInit } from '@angular/core';
import { SellStatusSectionData } from './sell-status-section.data';
import { timer } from 'rxjs';
import { Configuration } from '../../../../configuration/configuration';
import { SystemStatusType } from '../../../../core/interfaces/system-status-type.enum';
import { MatDialog } from '@angular/material';
import { SystemStatusDialogComponent } from '../../../../shared/components/system-status/system-status-dialog.component';

/**
 * @ignore
 */
@Component({
    selector: 'app-sell-status-section',
    templateUrl: './sell-status-section.component.html'
  })

  export class SellStatusSectionComponent implements OnInit {

    @Input()
    data: SellStatusSectionData;

    date = Date.now();
    timer: number;

    constructor(protected dialog: MatDialog) {
    }

    ngOnInit(): void {
        timer( 1000, 1000 ).subscribe( () => {
            if ( this.data.timestampBegin ) {
                this.timer = (Date.now() - this.data.timestampBegin) / 1000;
            }
            this.date = Date.now();
        });
    }

    showRegisterStatus(): boolean {
        if (this.data.systemStatus && Configuration.showRegisterStatus) {
            return Configuration.offlineOnlyRegisterStatus ?
                this.data.systemStatus.overallSystemStatus === SystemStatusType.Offline : true;
        } else {
            return false;
        }
    }

    onRegisterStatusClick(): void {
        if (Configuration.clickableRegisterStatus) {
            const dialogRef = this.dialog.open(SystemStatusDialogComponent, {
                width: '40%',
                data: {
                    devices: this.data.systemStatus.devices,
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
