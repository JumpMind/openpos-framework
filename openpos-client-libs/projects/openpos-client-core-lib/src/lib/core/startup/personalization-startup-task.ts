import {catchError,
    filter,
    map,
    retryWhen,
    switchMap,
    take,
    timeout,
    first,
    flatMap,
    last
} from 'rxjs/operators';
import {IStartupTask} from './startup-task.interface';
import {PersonalizationService} from '../personalization/personalization.service';
import {concat, defer, iif, interval, Observable, of, throwError} from 'rxjs';
import { MatDialog } from '@angular/material';
import { StartupTaskNames } from './startup-task-names';
import { Injectable } from '@angular/core';
import { StartupTaskData } from './startup-task-data';
import { Params } from '@angular/router';
import { PersonalizationComponent } from '../personalization/personalization.component';
import { merge } from 'rxjs';
import { Zeroconf, ZeroconfService, ZEROCONF_TOKEN } from './zeroconf/zeroconf';
import { Inject } from '@angular/core';
import { Optional } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class PersonalizationStartupTask implements IStartupTask {

    private readonly TYPE = '_jmc-personalize._tcp.';
    private readonly DOMAIN = 'local.';
    name = StartupTaskNames.PERSONALIZATION;

    order = 500;

    constructor(
        protected personalization: PersonalizationService, 
        protected matDialog: MatDialog,
        @Inject(ZEROCONF_TOKEN) @Optional() protected mdns: Array<Zeroconf>,
    ) {}

    execute(data: StartupTaskData): Observable<string> {
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
                () => this.mdns && this.mdns.length > 0,
                
                defer(
                    () => merge(...this.mdns.map(m => m.isAvailable().pipe(
                        take(1),
                        map(avail => ({ provider: m, avail })),
                        filter(m => m.avail),
                        map(m => m.provider)
                    ))).pipe(
                        lastOrElse(
                            provider => this.doAutoPersonalization(data, provider),
                            this.doStandardPersonalization(data),
                        ),
                    )
                ),

                // else
                defer(() => this.doStandardPersonalization(data))
            ),
        );
    }

    private doStandardPersonalization(
        data: StartupTaskData, 
        defaultData?: { serverAddress?: string, serverPort?: string, appId?: string }
    ): Observable<string> {
        return iif(
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
                            panelClass: 'openpos-default-theme',
                            data: defaultData
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
        );
    }

    private doAutoPersonalization(data: StartupTaskData, provider: Zeroconf): Observable<string> {
        return concat(
            of('attempting auto personalization'),
            provider.watch(this.TYPE, this.DOMAIN).pipe(
                first(r => r.action === 'resolved'),
                switchMap(r => provider.deviceName().pipe(
                    map(dn => ({ deviceName: dn, service: r.service }))
                )),
                take(1),
                timeout(10000),
                switchMap(p => this.attemptAutoPersonalize(data, p.service, p.deviceName)),
                catchError(e => {
                    console.error('failed to auto personalize', e);

                    return concat(
                        of('failed to auto personalize; fallback to standard personalization'),
                        this.doStandardPersonalization(data)
                    )
                })
            )
        );
    }

    private attemptAutoPersonalize(startupData: StartupTaskData, serviceConfig: ZeroconfService, deviceName: string): Observable<string> {
        return this.personalization.getAutoPersonalizationParameters(deviceName, serviceConfig)
            .pipe(
                flatMap(info => {
                    if (info) {
                        let params = info.personalizationParams;
                        let paramsMap: Map<string, string> = undefined;

                        if (params) {
                            paramsMap = new Map<string, string>();

                            for(let key in params) {
                                paramsMap.set(key, params[key]);
                            }
                        }

                        return this.personalization.personalize(
                            info.serverAddress,
                            info.serverPort,
                            info.deviceId,
                            info.appId,
                            paramsMap,
                            info.sslEnabled
                        ).pipe(
                            catchError(e => { 
                                console.error('failed to personalize from auto personalization data', e);

                                return concat(
                                    of('failed to auto personalize; falling back to standard pesronalization'),
                                    this.doStandardPersonalization(
                                        startupData, 
                                        { 
                                            serverAddress: info.serverAddress, 
                                            serverPort: info.serverPort, 
                                            appId: info.appId 
                                        })
                                    )
                            }),
                        );
                    }

                    return this.doStandardPersonalization(startupData);
                }),
                catchError(e => {
                    return this.doStandardPersonalization(startupData);
                }),
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

function lastOrElse<T, R>(valueProject: (value: R) => Observable<T>, e: Observable<T>): (source: Observable<R>) => Observable<T> {
    return (source) => new Observable(observer => {
        
        let path = e;

        let sourceSubscription = source.pipe(
            last(),
        ).subscribe({
            next: value => {
                path = valueProject(value);
            }
        });

        const pathSubscription = path.subscribe({
            next: iv => observer.next(iv),
            error: ie => observer.error(ie),
            complete: () => observer.complete()
        });

        return () => {
            if (pathSubscription) {
                pathSubscription.unsubscribe();
            }

            if (sourceSubscription) {
                sourceSubscription.unsubscribe();
            }
        };
    });
}
