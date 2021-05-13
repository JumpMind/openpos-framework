import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { Observable, ReplaySubject } from 'rxjs';
import { filter, map, mergeMap, take, tap } from 'rxjs/operators';

import { ConfigChangedMessage } from '../../core/messages/config-changed-message';
import { MessageTypes } from '../../core/messages/message-types';
import { StatusMessage } from './status.message';
import { SessionService } from '../../core/services/session.service';
import { StatusDetailsComponent } from './status-details/status-details.component';
import { StatusDetailsService } from "./status-details/status-details.service";

@Injectable({
    providedIn: 'root'
})
export class StatusService {

    private systemInfoConfig$ = new ReplaySubject<ConfigChangedMessage>(1);
    private latestStatus = new Map<string, StatusMessage>();
    private latestStatus$ = new ReplaySubject<Map<string, StatusMessage>>(1);

    private detailsDialog?: MatDialogRef<StatusDetailsComponent>;

    constructor(
        sessionService: SessionService,
        private dialog: MatDialog,
        public statusDetailsService : StatusDetailsService
    ) {
        sessionService.getMessages(MessageTypes.STATUS).pipe(
            tap(message => console.log("Status Updated", message))
        ).subscribe(message => this.statusUpdated(message));
        sessionService.getMessages(MessageTypes.CONFIG_CHANGED).pipe(
            filter(message => (message as ConfigChangedMessage).configType === 'SystemInfo'),
            tap(message => console.log("SystemInfo Updated ", message))
        ).subscribe(message => this.configUpdated(message));
        sessionService.getMessages(MessageTypes.CLOSE_STATUS_DETAILS).subscribe(() => { this.closeDetails(); });
    }

    public openDetails() {
        if (this.detailsDialog) {
            return;
        }

        this.statusDetailsService.isDetailsNotEmpty().pipe(
          take(1),
          filter(isDetailsNotEmpty => isDetailsNotEmpty),
          map(() => {
              this.detailsDialog = this.dialog.open(StatusDetailsComponent, { minWidth: '75%' })
              return this.detailsDialog;
          }),
          mergeMap(dialog => dialog.afterClosed())
        ).subscribe({
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

    private configUpdated(message: ConfigChangedMessage) {
        this.systemInfoConfig$.next(message);
    }

    private statusUpdated(message: StatusMessage) {
        this.latestStatus.set(message.id, message);

        this.latestStatus$.next(this.latestStatus);
    }
}
