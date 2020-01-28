import {catchError, map} from 'rxjs/operators';
import { IStartupTask } from './startup-task.interface';
import { PersonalizationService } from '../personalization/personalization.service';
import {Observable, of, Subject, throwError} from 'rxjs';
import { MatDialog } from '@angular/material';
import { StartupTaskNames } from './startup-task-names';
import { Injectable } from '@angular/core';
import { StartupTaskData } from './startup-task-data';
import { Params } from '@angular/router';
import { PersonalizationComponent } from '../personalization/personalization.component';

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

            message.next('Attempting to personalize using query parameters');


            this.personalizeFromQueueParams(data.route.queryParams, message)
                .pipe(
                    catchError( error => {
                        message.next(error);
                        message.next('Attempting to personalize from saved token')
                        return this.personalization.personalizeFromSavedSession();
                    })
                ).subscribe(
                {
                    error: (error) => {
                            message.next(error);
                            message.next('Launching personalization screen.');
                            this.personalization.personalizeFromSavedSession()
                            this.matDialog.open(
                                PersonalizationComponent, {
                                    disableClose: true,
                                    hasBackdrop: false,
                                    panelClass: 'openpos-default-theme'
                                }
                            ).afterClosed().subscribe(() => {
                                message.complete();
                            });
                        },
                    next: (result) => {
                        message.next(result);
                        message.complete();
                    }
                }
            );
        });
    }

    personalizeFromQueueParams(queryParams: Params, message: Subject<string>) : Observable<string>{
        const deviceId = queryParams.deviceId;
        const appId = queryParams.appId;
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

            return this.personalization.personalize(serverName, serverPort, deviceId, appId, personalizationProperties, sslEnabled);
        }

        return throwError('Personalizing using Query Params failed');
    }
}
