import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { BehaviorSubject, Observable, ReplaySubject, Subscription } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

import { ConfigChangedMessage } from '../messages/config-changed-message';
import { MessageTypes } from '../messages/message-types';
import { StatusMessage } from './status.message';
import { SessionService } from '../services/session.service';
import { PeripheralDeviceSelectionMessage } from '../messages/peripheral-device-selection';
import { StatusDetailsComponent } from './status-details/status-details.component';

@Injectable({
    providedIn: 'root'
})
export class StatusService {

    private systemInfoConfig$ = new ReplaySubject<ConfigChangedMessage>(1);
    private latestStatus = new Map<string, StatusMessage>();
    private latestStatus$ = new ReplaySubject<Map<string, StatusMessage>>(1);
    private peripheralSelections = new Map<string, PeripheralDeviceSelectionMessage>();
    private peripheralSelections$ = new BehaviorSubject<Map<string, PeripheralDeviceSelectionMessage>>(this.peripheralSelections);

    private detailsDialog?: MatDialogRef<StatusDetailsComponent>;

    constructor(
        sessionService: SessionService,
        private dialog: MatDialog
    ) {
        sessionService.getMessages(MessageTypes.STATUS).pipe(
            tap(message => console.log("Status Updated", message))
        ).subscribe(message => this.statusUpdated(message));
        sessionService.getMessages(MessageTypes.CONFIG_CHANGED).pipe(
            filter(message => (message as ConfigChangedMessage).configType === 'SystemInfo'),
            tap(message => console.log("SystemInfo Updated ", message))
        ).subscribe(message => this.configUpdated(message));

        sessionService.getMessages(MessageTypes.PERIPHERAL_DEVICE_SELECTION)
            .pipe(
                map(m => m as PeripheralDeviceSelectionMessage),
                filter(m => !!m.category)
            )
            .subscribe(m => {
                this.peripheralSelections.set(m.category, m);
                this.peripheralSelections$.next(this.peripheralSelections);
            });
    }

    public openDetails() {
        if (this.detailsDialog) {
            return;
        }

        this.detailsDialog = this.dialog.open(StatusDetailsComponent);
        this.detailsDialog
            .afterClosed()
            .subscribe({

                // don't need to worry about the subscription because the
                // observable will be automatically completed by the
                // source
                complete: () => {
                    this.detailsDialog = undefined;
                }
            });
    }

    public closeDetails() {
        if (this.detailsDialog) {
            this.detailsDialog.close();
        }
    }

    public getStatus(): Observable<Map<string, StatusMessage>> {
        return this.latestStatus$;
    }

    public getSystemInfo(): Observable<ConfigChangedMessage> {
        return this.systemInfoConfig$;
    }

    public getPeripheralSelections(): Observable<Map<String, PeripheralDeviceSelectionMessage>> {
        return this.peripheralSelections$;
    }

    private configUpdated(message: ConfigChangedMessage) {
        this.systemInfoConfig$.next(message);
    }

    private statusUpdated(message: StatusMessage) {
        this.latestStatus.set(message.id, message);

        this.latestStatus$.next(this.latestStatus);
    }
}
