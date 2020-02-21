import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationService } from '../personalization/personalization.service';
import { DiscoveryResponse } from './discovery-response.interface';
import {timeout, catchError, distinctUntilChanged} from 'rxjs/operators';
import {of, BehaviorSubject, Observable, combineLatest} from 'rxjs';
import { DiscoveryParams } from './discovery-params.interface';

@Injectable({
    providedIn: 'root',
})
export class DiscoveryService {

    private serverBaseUrl: string;
    private websocketUrl: string;
    private serverBaseUrl$ = new BehaviorSubject<string>(null);
    private apiServerBaseUrl$ = new BehaviorSubject<string>(null);
    private deviceAppApiServerBaseUrl$ = new BehaviorSubject<string>(null);

    constructor(protected personalization: PersonalizationService,
                private http: HttpClient) {

        combineLatest(  personalization.getSslEnabled$(),
                        personalization.getServerName$(),
                        personalization.getServerPort$(),
                        personalization.getAppId$(),
                        personalization.getDeviceId$()
        ).subscribe(([sslEnabled, serverName, serverPort, appId, deviceId]) =>
            this.updateServerBaseUrl(serverName, serverPort, sslEnabled, appId, deviceId));

        combineLatest(  personalization.getAppId$().pipe(distinctUntilChanged()),
                        personalization.getDeviceId$()
            ).subscribe( ([appId, deviceId]) => this.deviceAppApiServerBaseUrl$.next(this.getDeviceAppApiServerBaseUrl(appId, deviceId)));

        combineLatest(  personalization.getIsManagedServer$(),
                        personalization.getSslEnabled$(),
                        personalization.getServerName$(),
                        personalization.getServerPort$()
            ).subscribe( ([isManagedServer, sslEnabled, serverName, serverPort]) => this.updateWebsocketUrl(isManagedServer, sslEnabled, serverName, serverPort));
    }

    public async isManagementServerAlive(): Promise<boolean> {
        let result = false;
        if (this.personalization.getIsManagedServer$().getValue()) {
            const url = `http${this.personalization.getSslEnabled$().getValue() ? 's' : ''}` +
                `://${this.personalization.getServerName$().getValue()}:${this.personalization.getServerPort$().getValue()}/ping`;
            const httpResult = await this.http.get(url, {responseType: 'text'})
                .pipe(timeout(1000),
                    catchError(e => {
                        return of(false);
                    })
                )
                .toPromise();
            result = httpResult != null;
        } else {
            console.warn(`Not a managed server`);
        }

        return result;
    }

    private makeParams(params?: DiscoveryParams): DiscoveryParams {
        return {
            server: params && typeof params.server !== 'undefined' ? params.server : this.personalization.getServerName$().getValue(),
            port: params && typeof params.port !== 'undefined' ? params.port : this.personalization.getServerPort$().getValue(),
            deviceId: params && typeof params.deviceId !== 'undefined' ? params.deviceId : this.personalization.getDeviceId$().getValue(),
            sslEnabled: params && typeof params.sslEnabled !== 'undefined' ? params.sslEnabled : this.personalization.getSslEnabled$().getValue(),
            appId: params && typeof params.appId !== 'undefined' ? params.appId : null,
            maxWaitMillis: params && typeof params.maxWaitMillis !== 'undefined' ? params.maxWaitMillis : 90000
        };
    }
    public async discoverDeviceProcess(parameters?: DiscoveryParams): Promise<DiscoveryResponse> {
        const params = this.makeParams(parameters);
        const url = `http${params.sslEnabled ? 's' : ''}://${params.server}:${params.port}/discover?` +
            `deviceId=${params.deviceId}${params.appId ? '&appId=' + params.appId : ''}`;
        console.info('Discovering device process using url: ' + url);

        let discoveryError: any = null;
        try {
            const httpResult = await this.http.get<DiscoveryResponse>(url, {})
                .pipe(timeout(params.maxWaitMillis),
                    catchError(e => {
                        return of({ success: false, message: e.message });
                    })
                )
                .toPromise();
            if (httpResult) {
                if (httpResult.hasOwnProperty('success')) {
                    if (! httpResult.success) {
                        console.log('Discovery FAILED with url: ' + url);
                    }
                } else {
                    httpResult.success = true;
                }
                if (httpResult.success) {
                    console.log('Successful Discovery with url: ' + url);
                    const discoveryResp = httpResult as DiscoveryResponse;
                    if (params.sslEnabled) {
                        this.serverBaseUrl = discoveryResp.secureWebServiceBaseUrl;
                        this.websocketUrl = discoveryResp.secureWebSocketBaseUrl;
                    } else {
                        this.serverBaseUrl = discoveryResp.webServiceBaseUrl;
                        this.websocketUrl = discoveryResp.webSocketBaseUrl;
                    }
                }
                return httpResult;
            } else {
                discoveryError = { success: false, message: '?' };
                return discoveryError;
            }
        } catch (error) {
            discoveryError = error;
            discoveryError['success'] = false;
            return discoveryError;
        }
    }

    public getServerBaseURL(): string {
        if (this.personalization.getIsManagedServer$().getValue()) {
            console.debug(`serverBaseURL isn't set yet for the managed server`);
        }
        return this.serverBaseUrl;
    }

    private updateServerBaseUrl( serverName: string, serverPort: string, sslEnabled: boolean, appId: string, deviceId: string) {
        if( !serverName || !serverPort || !appId || !deviceId ) return;

        const protocol = sslEnabled ? 'https' : 'http';
        this.serverBaseUrl = `${protocol}://${serverName}` +
            `${serverPort ? `:${serverPort}` : ''}`;
        console.info(`Generated serverBaseURL: ${this.serverBaseUrl}`);
        this.serverBaseUrl$.next(this.getServerBaseURL());
        this.apiServerBaseUrl$.next(this.getApiServerBaseURL());
        this.deviceAppApiServerBaseUrl$.next(this.getDeviceAppApiServerBaseUrl(appId, deviceId));
    }

    public getServerBaseURL$(): Observable<string> {
        return this.serverBaseUrl$;
    }

    public getApiServerBaseUrl$(): Observable<string> {
        return this.apiServerBaseUrl$;
    }

    private getDeviceAppApiServerBaseUrl(appId: string, deviceId: string): string {
        return `${this.getApiServerBaseURL()}/appId/${appId}/deviceId/${deviceId}`;
    }

    public getDeviceAppApiServerBaseUrl$(): Observable<string> {
        return this.deviceAppApiServerBaseUrl$;
    }

    public getApiServerBaseURL(): string {
        return `${this.getServerBaseURL()}/api`;
    }

    private updateWebsocketUrl( isManageServer: boolean, sslEnabled: boolean, serverName: string, serverPort: string) {
        if (isManageServer) {
            console.debug(`webSocketUrl isn't set yet for the managed server`);
        } else {
            let protocol = 'ws://';
            if (sslEnabled) {
                protocol = 'wss://';
            }
            let url: string = protocol + serverName;
            if (serverPort) {
                url = url + ':' + serverPort;
            }
            url = url + '/api/websocket';
            this.websocketUrl = url;
        }
    }

    public getWebsocketUrl(): string {
        return this.websocketUrl;
    }

}
