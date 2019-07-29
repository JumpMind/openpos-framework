import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PersonalizationResult } from '../interfaces/personalization-result.interface';

@Injectable({
    providedIn: 'root',
})
export class ClientUrlService {

    private _clientUrl: string;
    get clientUrl(): string {
        return this._clientUrl;
    }

    constructor(private router: Router, private http: HttpClient) { }

    private getClientUrl(clientName: string, clientPort: string, appName: string, clientSslEnabled: boolean): string {
        let clientUrl = clientSslEnabled ? 'https://' : 'http://';
        clientUrl += clientName + ':' + clientPort + '/#/' + appName;

        return clientUrl;
    }

    public async checkClientStatus(clientName: string, clientPort: string, appName: string, clientSslEnabled: boolean): Promise<any> {

        this._clientUrl = this.getClientUrl(clientName, clientPort, appName, clientSslEnabled);

        let clientError: any = null;
        try {
            const httpResult = await this.http.get(this.clientUrl, { responseType: 'text' }).toPromise();
            if (httpResult) {
                console.log('Client Url Online: ' + this.clientUrl);
                return { success: true };
            } else {
                clientError = { message: '?' };
            }
        } catch (error) {
            console.log('Client Url Offline: ' + this.clientUrl);
            console.log(error);
            clientError = error;
        }

        return { success: false, message: clientError.message };
    }

    private getQueryParameters(
        serverName: string, serverPort: string, serverSslEnabled: boolean,
        deviceId: string, personalizationResults: PersonalizationResult[]): string {

        let queryParams = '?serverName=' + serverName + '&serverPort=' + serverPort + '&sslEnabled=' + serverSslEnabled
            + '&deviceId=' + deviceId;

        if (personalizationResults) {
            for (const personalizationResult of personalizationResults) {
                queryParams += '&' + personalizationResult.property + '=' + personalizationResult.value;
            }
        }

        return queryParams;
    }

    public navigate(
        clientName: string, clientPort: string, appName: string, clientSslEnabled: boolean,
        serverName: string, serverPort: string, serverSslEnabled: boolean, deviceId: string,
        personalizationResults: PersonalizationResult[]) {

        this._clientUrl = this.getClientUrl(clientName, clientPort, appName, clientSslEnabled);
        const queryParams = this.getQueryParameters(serverName, serverPort, serverSslEnabled, deviceId, personalizationResults);
        const url = this.clientUrl + queryParams;

        localStorage.setItem('clientUrl', url);

        this.router.navigate(
            ['/externalRedirect', { externalUrl: url }],
            { skipLocationChange: true }
        );
    }

    public renavigate() {
        const clientUrl = localStorage.getItem('clientUrl');
        if (clientUrl) {
            this.router.navigate(
                ['/externalRedirect', { externalUrl: clientUrl }],
                { skipLocationChange: true }
            );
        }
    }
}
