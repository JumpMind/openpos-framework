import { Injectable, Optional, Inject } from '@angular/core';
import { MatDialog } from '@angular/material';
import { concat, Observable, of } from 'rxjs';
import { catchError, filter, first, flatMap, map, mergeMap, switchMap, take } from 'rxjs/operators';
import { IStartupTask } from './startup-task.interface';
import { PersonalizationService } from '../personalization/personalization.service';
import { StartupTaskData } from './startup-task-data';
import { PersonalizationComponent } from '../personalization/personalization.component';
import { StartupTaskNames } from "./startup-task-names";
import { Zeroconf, ZeroconfService, ZEROCONF_TOKEN } from './zeroconf/zeroconf';

@Injectable({
    providedIn: 'root',
})
export class AutoPersonalizationStartupTask implements IStartupTask {
    name = StartupTaskNames.AUTO_PERSONALIZATION;
    order = 500;
    private readonly TYPE = '_jmc-personalize._tcp.';
    private readonly DOMAIN = 'local.';

    constructor(
        protected personalization: PersonalizationService,
        protected matDialog: MatDialog,
        @Inject(ZEROCONF_TOKEN) @Optional() protected mdns: Array<Zeroconf>
    ) {}

    execute(data: StartupTaskData): Observable<string> {
        if (this.mdns && this.mdns.length > 0) {
            if (this.personalization.hasSavedSession()) {
                return this.personalization.personalizeFromSavedSession();
            } else {
                return of(...this.mdns).pipe(
                    mergeMap(v => v.isAvailable().pipe(
                        take(1),
                        map(avail => ({ provider: v, avail }))
                    )),
                    filter(v => v.avail),
                    map(v => v.provider),
                    mergeMap(v => v.watch(this.TYPE, this.DOMAIN).pipe(
                        first(r => r.action === 'resolved'),
                        mergeMap(r => v.deviceName().pipe(
                            map(dn => ({deviceName: dn, service: r.service}))
                        ))
                    )),
                    take(1),
                    switchMap(p => this.attemptAutoPersonalize(p.service, p.deviceName))
                );
            }
        } else {
            return of("No auto personalization available for device");
        }

    }

    manualPersonalization(serverAddress?: string, serverPort?: string, appId?: string): Observable<string> {
        return concat(
            of("Auto-personalization failed, reverting to manual personalization"),
            this.matDialog.open(
                PersonalizationComponent, {
                    disableClose: true,
                    hasBackdrop: false,
                    panelClass: 'openpos-default-theme',
                    data: {
                        serverAddress,
                        serverPort,
                        appId
                    }
                }
            ).afterClosed().pipe(take(1)));
    }


    private attemptAutoPersonalize(serviceConfig: ZeroconfService, deviceName: string): Observable<string> {
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
                            catchError(() => this.manualPersonalization(info.serverAddress, info.serverPort, info.appId)),
                        );
                    }

                    return this.manualPersonalization();
                }),
                catchError(e => {
                    return this.manualPersonalization()
                }),
            )
    }
}
