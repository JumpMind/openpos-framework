import { IVersion } from './../interfaces/version.interface';
import { VERSION } from './../../version';
import { Injectable } from '@angular/core';
import { SessionService } from './session.service';
import { Configuration } from './../../configuration/configuration';
import { distinct, filter, groupBy, map, mergeMap, publish, share, tap } from 'rxjs/operators';
import { BehaviorSubject, ConnectableObservable, Observable, ReplaySubject } from 'rxjs';
import { ConfigChangedMessage } from '../messages/config-changed-message';
import { ThemeChangedMessage } from '../messages/theme-changed-message';
import { VersionsChangedMessage } from '../messages/versions-changed-message';
import { MessageTypes } from '../messages/message-types';

@Injectable({
    providedIn: 'root',
})
export class ConfigurationService {

    public versions: Array<IVersion> = [];
    public theme$ =  new BehaviorSubject<string>('openpos-default-theme');

    private config$ = new ReplaySubject<Map<string, ConfigChangedMessage>>(1);

    constructor(private sessionService: SessionService ) {
        const capturedConfig = new Map<string, ConfigChangedMessage>();
        this.sessionService.getMessages(MessageTypes.CONFIG_CHANGED).pipe(
            map(m => m as ConfigChangedMessage),
            filter(m => !!m)
        ).subscribe({
            next: c => {
                capturedConfig.set(c.configType, c);
                this.config$.next(capturedConfig);
            }
        });

        this.getConfiguration('uiConfig').subscribe( m => this.mapConfig(m));
        this.getConfiguration<ThemeChangedMessage>('theme').subscribe( m => this.theme$.next(m.name));
        this.getConfiguration<VersionsChangedMessage>('versions').subscribe( m => {
            this.versions = m.versions.map( v => v);
            this.versions.push(VERSION as IVersion);
        });
    }

    public getConfiguration<T extends ConfigChangedMessage>(configType: string): Observable<T> {
        return this.config$.pipe(
            map(m => m.get(configType) as T),
            filter(m => m && m.configType === configType),
            distinct()
        );
    }

    protected mapConfig(response: any) {
        for (const p of Object.keys(response)) {
            if (Configuration.hasOwnProperty(p)) {
                const configPropertyType = typeof Configuration[p];
                const responsePropertyType = typeof response[p];
                try {
                    if (configPropertyType !== responsePropertyType) {
                        if (configPropertyType === 'string') {
                            Configuration[p] = response[p].toString();
                        } else {
                            Configuration[p] = JSON.parse(response[p]);
                        }
                    } else {
                        Configuration[p] = response[p];
                    }
                } catch (e) {
                    console.warn(`Failed to convert configuration response property '${p}' with value [${response[p]}] ` +
                      `and type '${responsePropertyType}' to Configuration[${p}] of type '${configPropertyType}'` +
                      ` Error: ${e}`);
                }
            }
        }
    }

}
