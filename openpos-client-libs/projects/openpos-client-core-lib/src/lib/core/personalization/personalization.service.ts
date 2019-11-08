import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationResponse } from './personalization-response.interface';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class PersonalizationService {
    static readonly OPENPOS_MANAGED_SERVER_PROPERTY = 'managedServer';
    private baseUrlAttributeChanged$ = new BehaviorSubject<boolean>(false);
    private appIdChanged$ = new BehaviorSubject<boolean>(false);

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

        this.baseUrlAttributeChanged$.next(true);
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

    public getAppIdChanged$(): Observable<boolean> {
        return this.appIdChanged$;
    }

    public getBaseUrlAttributeChanged$(): Observable<boolean> {
        return this.baseUrlAttributeChanged$;
    }

    public getPersonalizationProperties$(): Observable<Map<string, string>> {
        return this.personalizationProperties$;
    }

    public isManagedServer(): boolean {
        return 'true' === localStorage.getItem(PersonalizationService.OPENPOS_MANAGED_SERVER_PROPERTY);
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
        const appIdChanged = id !== this.appId;
        this.appId = id;
        if (appIdChanged) {
            this.appIdChanged$.next(true);
        }
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


