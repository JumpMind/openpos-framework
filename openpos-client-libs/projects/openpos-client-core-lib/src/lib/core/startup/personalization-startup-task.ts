import { IStartupTask } from './startup-task.interface';
import { PersonalizationService } from '../services/personalization.service';
import { Observable, Subject } from 'rxjs';
import { MatDialog } from '@angular/material';
import { StartupTaskNames } from './startup-task-names';
import { Injectable } from '@angular/core';
import { StartupTaskData } from './startup-task-data';
import { Params } from '@angular/router';
import { PersonalizationComponent } from '../components/personalization/personalization.component';

@Injectable({
    providedIn: 'root',
})
export class PersonalizationStartupTask implements IStartupTask {

    name = StartupTaskNames.PERSONALIZATION;

    order = 500;

    constructor(protected personalization: PersonalizationService, protected matDialog: MatDialog) {

    }

    execute(data: StartupTaskData): Observable<string> {
        return Observable.create((message: Subject<string>) => {
            this.personalizeFromQueueParams(data.route.queryParams, message);
            message.next('Checking if device is personalized.');
            if (!this.personalization.isPersonalized()) {
                message.next('Launching personalization screen.');
                this.matDialog.open(
                    PersonalizationComponent, {
                        disableClose: true,
                        hasBackdrop: false,
                        panelClass: 'openpos-default-theme'
                    }
                ).afterClosed().subscribe(() => {
                    message.complete();
                });
            } else {
                message.next('Device is already personalized.');
                message.complete();
            }
        });
    }

    personalizeFromQueueParams(queryParams: Params, message: Subject<string>) {
        const deviceId = queryParams.deviceId;
        const serverName = queryParams.serverName;
        let serverPort = queryParams.serverPort;
        let sslEnabled = queryParams.sslEnabled;

        const personalizationProperties = new Map<string, string>();
        const keys = Object.keys(queryParams);
        if (keys) {
            for (const key of keys) {
                if (key !== 'deviceId' && key !== 'serverName' && key !== 'serverPort' && key !== 'sslEnabled') {
                    personalizationProperties.set(key, queryParams[key]);
                }
            }
        }

        if (deviceId && serverName) {
            message.next('Personalizing from query params');
            serverPort = !serverPort ? 6140 : serverPort;
            sslEnabled = !sslEnabled ? false : sslEnabled;

            this.personalization.personalize(serverName, serverPort, deviceId, personalizationProperties);
        }
    }


}
