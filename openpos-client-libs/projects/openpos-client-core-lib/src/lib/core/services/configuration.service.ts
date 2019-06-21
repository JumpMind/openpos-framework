import { IVersion } from './../interfaces/version.interface';
import { VERSION } from './../../version';
import { Injectable } from '@angular/core';
import { SessionService } from './session.service';
import { Logger } from './logger.service';
import { Configuration } from './../../configuration/configuration';
import { filter, tap } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ConfigurationService {

    public versions: Array<IVersion> = [];
    public theme$ =  new BehaviorSubject<string>('openpos-default-theme');

    constructor(private log: Logger, private sessionService: SessionService ) {
        this.sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType && (m.configType === 'uiConfig')),
            tap( m => this.log.info('Ui Config Changed: ' + JSON.stringify(m)))
        ).subscribe( m => this.mapConfig(m));

        this.sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType === 'theme'),
            tap( m => this.log.info('Config Changed Theme: ' + m.name ))
        ).subscribe( m => this.theme$.next(m.name));

        this.sessionService.getMessages('ConfigChanged').pipe(
            filter( m => m.configType === 'versions'),
        ).subscribe( m => {
            this.versions = m.versions.map( v => v);
            this.versions.push(VERSION as IVersion);
        });
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
                    this.log.warn(`Failed to convert configuration response property '${p}' with value [${response[p]}] ` +
                      `and type '${responsePropertyType}' to Configuration[${p}] of type '${configPropertyType}'` +
                      ` Error: ${e}`);
                }
            }
        }
    }

}
