import {catchError, first, flatMap, take, tap, timeout} from 'rxjs/operators';
import {IStartupTask} from './startup-task.interface';
import {PersonalizationService} from '../personalization/personalization.service';
import {concat, Observable, of} from 'rxjs';
import {MatDialog} from '@angular/material';
import {Injectable} from '@angular/core';
import {StartupTaskData} from './startup-task-data';
import {PersonalizationComponent} from '../personalization/personalization.component';
import {Zeroconf, ZeroconfService} from "@ionic-native/zeroconf";
import {StartupTaskNames} from "./startup-task-names";
import {WrapperService} from "../services/wrapper.service";
import {Configuration} from "../../configuration/configuration";


@Injectable({
    providedIn: 'root',
})
export class AutoPersonalizationStartupTask implements IStartupTask {
    name = StartupTaskNames.AUTO_PERSONALIZATION;
    order = 500;
    private readonly TYPE = '_jmc-personalize._tcp.';
    private readonly DOMAIN = 'local.';

    constructor(protected personalization: PersonalizationService, protected matDialog: MatDialog, protected wrapperService: WrapperService) {
    }

    execute(data: StartupTaskData): Observable<string> {
        if (this.personalization.shouldAutoPersonalize()) {
            if (this.personalization.hasSavedSession()) {
                return this.personalization.personalizeFromSavedSession().pipe(
                    catchError(e => {
                        this.logPersonalizationError(e);
                        return this.manualPersonalization();
                    }));
            } else {
                let name: string = null;
                let serviceConfig: ZeroconfService = null;

                console.log('Starting ZeroConf watch on device ', this.wrapperService.getDeviceName());

                return Zeroconf.watch(this.TYPE, this.DOMAIN).pipe(
                    timeout(Configuration.autoPersonalizationRequestTimeoutMillis),
                    first(conf => conf.action === 'resolved'),
                    tap(conf => {
                        serviceConfig = conf.service;
                        console.log('service resolved', conf.service);
                    }),
                    flatMap(() => this.wrapperService.getDeviceName()),
                    tap(deviceName => name = deviceName),
                    flatMap(() => {
                        const url = `${serviceConfig.hostname}:${serviceConfig.port}/${serviceConfig.txtRecord.path}`;
                        return this.attemptAutoPersonalize(url, name);
                    }),
                    catchError(e => {
                        this.logPersonalizationError(e);
                        return this.personalizeWithHostname();
                    })
                );
            }
        } else {
            return of("No auto personalization available for device");
        }
    }


    personalizeWithHostname(): Observable<string> {
        const servicePath = Configuration.autoPersonalizationServicePath;
        if (!!servicePath) {
            let name: string = null;
            return concat(
                of("Attempting to retrieve personalization params via hostname"),
                this.wrapperService.getDeviceName().pipe(
                    tap(deviceName => name = deviceName),
                    flatMap(() => this.attemptAutoPersonalize(Configuration.autoPersonalizationServicePath, name)),
                    catchError(e => {
                        this.logPersonalizationError(e);
                        return this.manualPersonalization();
                    })),
            );
        } else {
            return this.manualPersonalization();
        }
    }

    manualPersonalization(): Observable<string> {
        return concat(
            of("Auto-personalization failed, reverting to manual personalization"),
            this.matDialog.open(
                PersonalizationComponent, {
                    disableClose: true,
                    hasBackdrop: false,
                    panelClass: 'openpos-default-theme'
                }
            ).afterClosed().pipe(take(1)));
    }


    private attemptAutoPersonalize(url: string, deviceName: string): Observable<string> {
        return this.personalization.getAutoPersonalizationParameters(deviceName, url)
            .pipe(
                flatMap(info => {
                    if (info) {
                        return this.personalization.personalize(
                            info.serverAddress,
                            info.serverPort,
                            info.deviceId,
                            info.appId,
                            info.personalizationParams,
                            info.sslEnabled).pipe(
                            catchError(e => {
                                this.logPersonalizationError(e);
                                return this.manualPersonalization();
                            })
                        );
                    }
                    return this.manualPersonalization();
                }),
                catchError(e => {
                    this.logPersonalizationError(e);
                    return this.personalizeWithHostname();
                }));
    }

    private logPersonalizationError(error: any): void {
        console.log("Error during auto-personalization: " + JSON.stringify(error))
    }
}
