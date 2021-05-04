import {filter, retryWhen, switchMap, take, tap} from 'rxjs/operators';
import {IStartupTask} from './startup-task.interface';
import {PersonalizationService} from '../personalization/personalization.service';
import {concat, interval, merge, Observable, of, Subject, throwError} from 'rxjs';
import {MatDialog} from '@angular/material';
import {StartupTaskNames} from './startup-task-names';
import {Injectable} from '@angular/core';
import {StartupTaskData} from './startup-task-data';
import {Params} from '@angular/router';
import {PersonalizationComponent} from '../personalization/personalization.component';

@Injectable({
    providedIn: 'root',
})
export class PersonalizationStartupTask implements IStartupTask {

    name = StartupTaskNames.PERSONALIZATION;

    order = 500;

    constructor(protected personalization: PersonalizationService, protected matDialog: MatDialog) {

    }

    execute(data: StartupTaskData): Observable<string> {
        if (this.personalization.shouldAutoPersonalize()) {
            return of("Auto-personalizing Capacitor app");
        }
        if (this.hasPersonalizationQueryParams(data.route.queryParams) || this.personalization.hasSavedSession()) {

            let personalize$: Observable<string>;

            let messages = new Subject<string>();
            let attemptMessage;

            if (this.hasPersonalizationQueryParams(data.route.queryParams)) {
                attemptMessage = 'Attempting to personalize using query parameters';
                personalize$ = this.personalizeFromQueueParams(data.route.queryParams);
            } else if (this.personalization.hasSavedSession()) {

                attemptMessage = 'Attempting to personalize from saved token';
                personalize$ = this.personalization.personalizeFromSavedSession();
            }

            return concat(
                of(attemptMessage),
                merge(
                    messages,
                    personalize$.pipe(
                        retryWhen(errors =>
                            errors.pipe(
                                switchMap(() => interval(1000),
                                    (error, time) => `${error} \n Retry in ${5 - time}`),
                                tap(result => messages.next(result)),
                                filter(result => result.endsWith('0')),
                                tap(() => messages.next(attemptMessage))
                            )
                        ),
                        take(1),
                        tap(() => messages.complete())
                    ))
            );

        }

        return concat(
            of("No saved session found prompting manual personalization"),
            this.matDialog.open(
                PersonalizationComponent, {
                    disableClose: true,
                    hasBackdrop: false,
                    panelClass: 'openpos-default-theme'
                }
            ).afterClosed().pipe(take(1)));
    }

    hasPersonalizationQueryParams(queryParams: Params): boolean {
        return queryParams.deviceId && queryParams.appId && queryParams.serverName && queryParams.serverPort;

    }

    personalizeFromQueueParams(queryParams: Params): Observable<string> {
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
            serverPort = !serverPort ? 6140 : serverPort;
            sslEnabled = !sslEnabled ? false : sslEnabled;

            return this.personalization.personalize(serverName, serverPort, deviceId, appId, personalizationProperties, sslEnabled);
        }

        return throwError('Personalizing using Query Params failed');
    }
}
