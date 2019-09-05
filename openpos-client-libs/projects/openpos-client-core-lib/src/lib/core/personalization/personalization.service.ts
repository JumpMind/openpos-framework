import { Logger } from '../services/logger.service';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationResponse } from './personalization-response.interface';

@Injectable({
    providedIn: 'root',
})
export class PersonalizationService {

    private serverBaseUrl: string;

    constructor(private log: Logger, private http: HttpClient) {
    }

    public personalize(
        serverName: string, serverPort: string, deviceId: string,
        personalizationProperties?: Map<string, string>, sslEnabled?: boolean) {

        this.log.info(`personalizing with server: ${serverName}, port: ${serverPort}, deviceId: ${deviceId}`);
        localStorage.setItem('serverName', serverName);
        localStorage.setItem('serverPort', serverPort);
        localStorage.setItem('deviceId', deviceId);
        this.setPersonalizationProperties(personalizationProperties);

        if (sslEnabled) {
            localStorage.setItem('sslEnabled', '' + sslEnabled);
        } else {
            localStorage.setItem('sslEnabled', 'false');
        }
        this.serverBaseUrl = null; // will be regenerated on next fetch
    }

    private setPersonalizationProperties(personalizationProperties?: Map<string, string>) {
        if (personalizationProperties) {
            const keys = Array.from(personalizationProperties.keys());

            localStorage.setItem('personalizationProperties', JSON.stringify(keys));

            for (const key of keys) {
                localStorage.setItem(key, personalizationProperties.get(key));
            }
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

    public setSslEnabled(enabled: boolean) {
        localStorage.setItem('sslEnabled', enabled + '');
    }

    public setServerName(name: string) {
        localStorage.setItem('serverName', name);
    }

    public getServerName(): string {
        return localStorage.getItem('serverName');
    }

    public getDeviceName(): string {
        return localStorage.getItem('deviceName');
    }

    public setDeviceName(deviceName: string): void {
        localStorage.setItem('deviceName', deviceName);
    }

    public setServerPort(port: string) {
        localStorage.setItem('serverPort', port);
    }

    public getServerPort(): string {
        return localStorage.getItem('serverPort');
    }

    public getDeviceId(): string {
        return localStorage.getItem('deviceId');
    }

    public setDeviceId(id: string) {
        localStorage.setItem('deviceId', id);
    }

    public getPersonalizationResults(): string {
        return localStorage.getItem('personalizationResults');
    }

    public setPersonalizationResults(personalizationResults: string) {
        localStorage.setItem('personalizationResults', personalizationResults);
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
            const protocol = this.isSslEnabled() ? 'https' : 'http';
            this.serverBaseUrl = `${protocol}://${this.getServerName()}${this.getServerPort() ? `:${this.getServerPort()}` : ''}`;
            this.log.info(`Generated serverBaseURL: ${this.serverBaseUrl}`);
        }
        return this.serverBaseUrl;
    }

    public getApiServerBaseURL(): string {
        return `${this.getServerBaseURL()}/api`;
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


