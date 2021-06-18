import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Zeroconf, ZeroconfResult, ZeroconfService } from './zeroconf';

import { Service } from 'mdns';
import { ElectronService } from 'ngx-electron';

const ipv4Matcher = /^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
const ipv6Matcher = /^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$/

@Injectable()
export class MDnsZeroconf implements Zeroconf {
    constructor(private _electron: ElectronService) {}
    
    isAvailable(): Observable<boolean> {
        return of(this._electron.isElectronApp);
    }

    watch(type: string, domain: string): Observable<ZeroconfResult> {
        if (!this._electron.isElectronApp) {
            return of();
        }

        return new Observable(observer => {
            const upListener = (event, service) => {
                console.log('got service', service);
                observer.next({
                    action: 'resolved',
                    service: MDnsZeroconf._makeService(JSON.parse(service))
                });
            };

            const downListener = (event, service) => {
                observer.next({
                    action: 'removed',
                    service: MDnsZeroconf._makeService(JSON.parse(service))
                });
            };

            console.log('about to start mdns');

            let prefix = this._electron.ipcRenderer.sendSync('start-mdns', type, domain);

            console.log('started', prefix);

            this._electron.ipcRenderer.on(`${prefix}-service-up`, upListener);

            this._electron.ipcRenderer.on(`${prefix}-service-down`, downListener);

            return () => {
                this._electron.ipcRenderer.send('stop-mdns');
                this._electron.ipcRenderer.removeAllListeners(`${prefix}-service-up`);
                this._electron.ipcRenderer.removeAllListeners(`${prefix}-service-down`);
            };
        });
    }

    deviceName(): Observable<string> {
        return new Observable(observer => {
            const hostname = this._electron.ipcRenderer.sendSync('get-hostname');

            if (hostname) {
                observer.next(hostname);
                observer.complete();
            } else {
                observer.error('failed to retrieve hostname for this device');
            }
        });
    }

    private static _makeService(service: Service): ZeroconfService {
        return {
            name: service.name,
            domain: service.replyDomain,
            hostname: service.host,
            port: service.port,
            type: service.type.name,
            ipv4Addresses: service.addresses
                .filter(a => ipv4Matcher.test(a)),
            ipv6Addresses: service.addresses
                .filter(a => ipv6Matcher.test(a)),
            txtRecord: service.txtRecord
        };
    }
}
