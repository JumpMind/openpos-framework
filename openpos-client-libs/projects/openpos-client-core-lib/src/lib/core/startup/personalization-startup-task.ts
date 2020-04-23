import {
    debounceTime,
    delay, filter,
    first,
    mapTo,
    retryWhen,
    skip,
    skipUntil,
    skipWhile,
    switchMap,
    take,
    tap
} from 'rxjs/operators';
import { IStartupTask } from './startup-task.interface';
import { PersonalizationService } from '../personalization/personalization.service';
import {concat, interval, merge, Observable, of, Subject, throwError, timer} from 'rxjs';
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

        if(this.hasPersonalizationQueryParams(data.route.queryParams)){
            return concat( of('Attempting to personalize using query parameters'),
            this.personalizeFromQueueParams(data.route.queryParams)
                .pipe(take(1)));
        }

        if(this.personalization.hasSavedSession()){
            let messages = new Subject<string>();

            return concat(
                        of('Attempting to personalize from saved token'),
                        merge(
                            messages,
                            this.personalization.personalizeFromSavedSession().pipe(
                                retryWhen( errors =>
                                    errors.pipe(
                                        switchMap( () => interval(1000),
                                            (error, time) => `${error} \n Retry in ${5-time}`),
                                        tap(result => messages.next(result)),
                                        filter( result => result.endsWith('0')),
                                        tap( () => messages.next('Attempting to personalize from saved token'))
                                    )
                                ),
                                take(1),
                                tap(() => messages.complete())
                        ))
            );
        }

        return this.matDialog.open(
            PersonalizationComponent, {
                disableClose: true,
                hasBackdrop: false,
                panelClass: 'openpos-default-theme'
            }
        ).afterClosed().pipe(take(1));
    }

    hasPersonalizationQueryParams(queryParams: Params): boolean {
        return queryParams.deviceId && queryParams.appId && queryParams.servername && queryParams.serverPort;

    }

    personalizeFromQueueParams(queryParams: Params) : Observable<string>{
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
