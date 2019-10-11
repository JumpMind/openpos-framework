import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationResponse } from './personalization-response.interface';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class PersonalizationService {

    private serverBaseUrl: string;
    private serverBaseUrl$ = new BehaviorSubject<string>(this.getServerBaseURL());
    private apiServerBaseUrl$ = new BehaviorSubject<string>(this.getApiServerBaseURL());
    private deviceAppApiServerBaseUrl$ = new BehaviorSubject<string>(this.getDeviceAppApiServerBaseUrl());
    private personalizationProperties$ = new BehaviorSubject<Map<string, string>>(this.getPersonalizationProperties());
    private appId: string;

    constructor(private http: HttpClient) {
    }

    public personalize(
        serverName: string, serverPort: string, deviceId: string,
        personalizationProperties?: Map<string, string>, sslEnabled?: boolean) {

        console.info(`personalizing with server: ${serverName}, port: ${serverPort}, deviceId: ${deviceId}`);
        this.setServerName(serverName);
        this.setServerPort(serverPort);
        this.setDeviceId(deviceId);
        this.setPersonalizationProperties(personalizationProperties);

        if (sslEnabled) {
            this.setSslEnabled(sslEnabled);
        } else {
            this.setSslEnabled(false);
        }

        this.updateServerBaseUrl();
        this.serverBaseUrl$.next(this.serverBaseUrl);
        this.apiServerBaseUrl$.next(this.getApiServerBaseURL());
        this.deviceAppApiServerBaseUrl$.next(this.getDeviceAppApiServerBaseUrl());
    }

    private setPersonalizationProperties(personalizationProperties?: Map<string, string>) {
        if (personalizationProperties) {
            const keys = Array.from(personalizationProperties.keys());

            localStorage.setItem('personalizationProperties', JSON.stringify(keys));

            for (const key of keys) {
                localStorage.setItem(key, personalizationProperties.get(key));
            }

            this.personalizationProperties$.next(this.getPersonalizationProperties());
        }
    }

    private removePersonalizationProperties() {
        const keyString = localStorage.getItem('personalizationProperties');
        if (keyString) {
            const keys: string[] = JSON.parse(keyString);
            if (keys) {
                for (const key of keys) {
                    localStorage.removeItem(key);
                }
            }
        }
        localStorage.removeItem('personalizationProperties');

        this.personalizationProperties$.next(this.getPersonalizationProperties());
    }

    public dePersonalize() {
        localStorage.removeItem('serverName');
        localStorage.removeItem('serverPort');
        localStorage.removeItem('deviceId');
        localStorage.removeItem('theme');
        this.removePersonalizationProperties();
        localStorage.removeItem('sslEnabled');
    }

    public getPersonalizationProperties(): Map<string, string> {
        const map = new Map<string, string>();
        const keyString = localStorage.getItem('personalizationProperties');
        if (keyString) {
            const keys: string[] = JSON.parse(keyString);
            if (keys) {
                for (const key of keys) {
                    const value = localStorage.getItem(key);
                    map.set(key, value);
                }
            }
        }
        return map;
    }

    public getPersonalizationProperties$(): Observable<Map<string, string>> {
        return this.personalizationProperties$;
    }

    public getWebsocketUrl(): string {
        let protocol = 'ws://';
        if (this.isSslEnabled()) {
            protocol = 'wss://';
        }
        let url: string = protocol + this.getServerName();
        if (this.getServerPort()) {
            url = url + ':' + this.getServerPort();
        }
        url = url + '/api/websocket';
        return url;
    }

    public isSslEnabled(): boolean {
        return 'true' === localStorage.getItem('sslEnabled');
    }

    private setSslEnabled(enabled: boolean) {
        localStorage.setItem('sslEnabled', enabled + '');
    }

    private setServerName(name: string) {
        localStorage.setItem('serverName', name);
    }

    public getServerName(): string {
        return localStorage.getItem('serverName');
    }

    private setServerPort(port: string) {
        localStorage.setItem('serverPort', port);

        this.updateServerBaseUrl();
    }

    public getServerPort(): string {
        return localStorage.getItem('serverPort');
    }

    public getDeviceId(): string {
        return localStorage.getItem('deviceId');
    }

    private setDeviceId(id: string) {
        localStorage.setItem('deviceId', id);
    }

    public setAppId(id: string) {
        this.appId = id;
        this.deviceAppApiServerBaseUrl$.next(this.getDeviceAppApiServerBaseUrl());
    }

    public getAppId(): string {
        return this.appId;
    }

    public refreshApp() {
        window.location.reload();
    }

    public isPersonalized(): boolean {
        if (this.getServerName() && this.getDeviceId() && this.getServerPort()) {
            return true;
        } else {
            return false;
        }
    }

    public getServerBaseURL(): string {
        if (!this.serverBaseUrl) {
            this.updateServerBaseUrl();
        }
        return this.serverBaseUrl;
    }

    private updateServerBaseUrl() {
        const protocol = this.isSslEnabled() ? 'https' : 'http';
        this.serverBaseUrl = `${protocol}://${this.getServerName()}${this.getServerPort() ? `:${this.getServerPort()}` : ''}`;
        console.info(`Generated serverBaseURL: ${this.serverBaseUrl}`);
    }

    public getServerBaseURL$(): Observable<string> {
        return this.serverBaseUrl$;
    }

    public getApiServerBaseURL(): string {
        return `${this.getServerBaseURL()}/api`;
    }

    public getApiServerBaseUrl$(): Observable<string> {
        return this.apiServerBaseUrl$;
    }

    private getDeviceAppApiServerBaseUrl(): string {
        return `${this.getApiServerBaseURL()}/appId/${this.getAppId()}/deviceId/${this.getDeviceId()}`;
    }

    public getDeviceAppApiServerBaseUrl$(): Observable<string> {
        return this.deviceAppApiServerBaseUrl$;
    }

    public async requestPersonalization(serverName: string, serverPort: string, sslEnabled: boolean): Promise<PersonalizationResponse> {
        let url = sslEnabled ? 'https://' : 'http://';
        url += serverName + ':' + serverPort + '/personalize';

        console.log('Requesting Personalization with url: ' + url);

        let personalizeError: any = null;
        try {
            const httpResult = await this.http.get<PersonalizationResponse>(url, {}).toPromise();
            if (httpResult) {
                httpResult.success = true;
                console.log('Successful Personalization with url: ' + url);
                return httpResult;
            } else {
                personalizeError = { message: '?' };
            }
        } catch (error) {
            personalizeError = error;
        }

        if (personalizeError) {
            console.warn('bad validation of ' + url + ' with an error message of :' + personalizeError.message);
            return { success: false, message: personalizeError.message };
        }
    }
}


