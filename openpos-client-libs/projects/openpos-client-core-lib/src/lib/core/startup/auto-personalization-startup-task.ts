import {first, flatMap, take, tap} from 'rxjs/operators';
import {IStartupTask} from './startup-task.interface';
import {PersonalizationService} from '../personalization/personalization.service';
import {concat, from, Observable, of} from 'rxjs';
import {MatDialog} from '@angular/material';
import {Injectable} from '@angular/core';
import {StartupTaskData} from './startup-task-data';
import {PersonalizationComponent} from '../personalization/personalization.component';
import {DeviceInfo, Plugins} from "@capacitor/core";
import {Zeroconf, ZeroconfService} from "@ionic-native/zeroconf";
import {StartupTaskNames} from "./startup-task-names";

const {Device} = Plugins;

@Injectable({
    providedIn: 'root',
})
export class AutoPersonalizationStartupTask implements IStartupTask {
    name = StartupTaskNames.AUTO_PERSONALIZATION;
    order = 500;
    private readonly TYPE = '_jmc-personalize._tcp.';
    private readonly DOMAIN = 'local.';

    constructor(protected personalization: PersonalizationService, protected matDialog: MatDialog) {
    }

    execute(data: StartupTaskData): Observable<string> {
        if (this.personalization.shouldAutoPersonalize()) {
            let name: string = null;
            let serviceConfig: ZeroconfService = null;

            return Zeroconf.watch(this.TYPE, this.DOMAIN).pipe(
                first(conf => conf.action === 'resolved'),
                tap(conf => {
                    serviceConfig = conf.service;
                }),
                flatMap(() => from(Device.getInfo())),
                tap((info: DeviceInfo) => name = info.name),
                flatMap(() => this.attemptAutoPersonalize(serviceConfig, name))
            );
        } else {
            return of("No auto personalization available for device");
        }
    }

    manualPersonalization(): Observable<string> {
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


    private attemptAutoPersonalize(serviceConfig: ZeroconfService, deviceName: string): Observable<string> {
        return this.personalization.getDevicePersonalization(deviceName, serviceConfig)
            .pipe(
                flatMap(info => {
                    if (info.personalization) {
                        return this.personalization.personalize(
                            info.personalization.serverAddress,
                            info.personalization.serverPort,
                            info.personalization.deviceId,
                            info.personalization.appId,
                            info.personalization.personalizationParams,
                            info.personalization.sslEnabled);
                    }
                    return this.manualPersonalization();
                })
            )
    }
}
