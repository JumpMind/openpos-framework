import {catchError,
    filter,
    map,
    retryWhen,
    switchMap,
    take,
    timeout} from 'rxjs/operators';
import {IStartupTask} from './startup-task.interface';
import {PersonalizationService} from '../personalization/personalization.service';
import {concat, defer, iif, interval, Observable, of, throwError} from 'rxjs';
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

    constructor(protected personalization: PersonalizationService, protected matDialog: MatDialog) {}

    execute(data: StartupTaskData): Observable<string> {
        if (this.personalization.shouldAutoPersonalize()) {
            return of("Auto-personalizing client");
        }
        return concat(
            of('initializing personalization...'),
            this.personalization.personalizationInitialized$.pipe(
                filter(initialized => initialized),
                take(1),
                map(() => 'personalization initialized'),
                timeout(5000),
                catchError(() => of('timed out waiting for personalization to initialize'))
            ),

            iif(
                () => this.hasPersonalizationQueryParams(data.route.queryParams),

                concat(
                    of('personalizing from query params'),
                    defer(() => this.personalizeFromQueueParams(data.route.queryParams))
                ),

                // else
                iif(
                    () => this.personalization.hasSavedSession(),

                    concat(
                        of('personalizing from saved session'),
                        defer(() => this.personalization.personalizeFromSavedSession()),
                    ),

                    // else
                    concat(
                        of('prompting for manual personalization'),
                        defer(() => this.matDialog.open(
                            PersonalizationComponent,
                            {
                                disableClose: true,
                                hasBackdrop: false,
                                panelClass: 'openpos-default-theme'
                            }
                        ).afterClosed().pipe(
                            take(1)
                        ))
                    )
                )
            ).pipe(
                retryWhen(errors => errors.pipe(
                    switchMap(() => interval(1000), (error, time) => `${error} \n Retry in ${5-time}`),
                ))
            )
        );
    }

    hasPersonalizationQueryParams(queryParams: Params): boolean {
        return ((queryParams.deviceId && queryParams.appId && queryParams.serverName && queryParams.serverPort)
            || (queryParams.deviceToken && queryParams.serverName && queryParams.serverPort));

    }

    personalizeFromQueueParams(queryParams: Params): Observable<string> {
        const deviceId = queryParams.deviceId;
        const appId = queryParams.appId;
        const serverName = queryParams.serverName;
        const deviceToken = queryParams.deviceToken;
        let serverPort = queryParams.serverPort;
        let sslEnabled = queryParams.sslEnabled == 'true';

        const personalizationProperties = new Map<string, string>();
        const keys = Object.keys(queryParams);
        if (keys) {
            for (const key of keys) {
                if (key !== 'deviceId' && key !== 'deviceToken' && key !== 'serverName' && key !== 'serverPort' && key !== 'sslEnabled') {
                    personalizationProperties.set(key, queryParams[key]);
                }
            }
        }

        if (deviceToken && serverName) {
            serverPort = !serverPort ? 6140 : serverPort;
            sslEnabled = !sslEnabled ? false : sslEnabled;

            return this.personalization.personalizeWithToken(serverName, serverPort, deviceToken, sslEnabled);
        }

        if (deviceId && serverName) {
            serverPort = !serverPort ? 6140 : serverPort;
            sslEnabled = !sslEnabled ? false : sslEnabled;

            return this.personalization.personalize(serverName, serverPort, deviceId, appId, personalizationProperties, sslEnabled);
        }

        return throwError('Personalizing using Query Params failed');
    }
}
