import { IVersion } from './../interfaces/version.interface';
import { VERSION } from './../../version';
import { Injectable } from '@angular/core';
import { SessionService } from './session.service';
import { Configuration } from './../../configuration/configuration';
import { filter, tap } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
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

    constructor(private sessionService: SessionService ) {

        this.getConfiguration('uiConfig').subscribe( m => this.mapConfig(m));
        this.getConfiguration<ThemeChangedMessage>('theme').subscribe( m => this.theme$.next(m.name));
        this.getConfiguration<VersionsChangedMessage>('versions').subscribe( m => {
            this.versions = m.versions.map( v => v);
            this.versions.push(VERSION as IVersion);
        });
    }

    public getConfiguration<T extends ConfigChangedMessage>(configType: string): Observable<T> {
        return this.sessionService.getMessages(MessageTypes.CONFIG_CHANGED).pipe (
            filter( m => m.configType === configType),
            tap( m => console.info( `ConfigChanged for ${configType}: ${JSON.stringify(m)}`))
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
