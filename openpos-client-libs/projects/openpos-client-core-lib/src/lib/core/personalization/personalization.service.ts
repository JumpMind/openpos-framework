import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonalizationConfigResponse } from './personalization-config-response.interface';
import { Observable, BehaviorSubject, throwError, Subject, zip } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { PersonalizationRequest } from './personalization-request';
import { PersonalizationResponse } from './personalization-response.interface';

import { Capacitor, Plugins as CapacitorPlugins } from '@capacitor/core';

@Injectable({
    providedIn: 'root'
})
export class PersonalizationService {
    static readonly OPENPOS_MANAGED_SERVER_PROPERTY = 'managedServer';

    readonly personalizationInitialized$ = new BehaviorSubject<boolean>(false);

    private readonly personalizationProperties$ = new BehaviorSubject<Map<string, string> | null>(null);
    private readonly deviceId$ = new BehaviorSubject<string | null>(null);
    private readonly appId$ = new BehaviorSubject<string | null>(null);
    private readonly deviceToken$ = new BehaviorSubject<string | null>(null);
    private readonly serverName$ = new BehaviorSubject<string | null>(null);
    private readonly serverPort$ = new BehaviorSubject<string | null>(null);
    private readonly sslEnabled$ = new BehaviorSubject<boolean | null>(null);
    private readonly isManagedServer$ = new BehaviorSubject<boolean | null>(null);
    private readonly personalizationSuccessFul$ = new BehaviorSubject<boolean>(false);

    constructor(private http: HttpClient) {
        zip(
            PersonalizationService.updateStorageVariable(this.deviceToken$, 'deviceToken'),
            PersonalizationService.updateStorageVariable(this.serverName$, 'serverName'),
            PersonalizationService.updateStorageVariable(this.serverPort$, 'serverPort'),
            PersonalizationService.updateStorageVariable(this.sslEnabled$, 'sslEnabled', value => value === 'true'),
            PersonalizationService.updateStorageVariable(this.isManagedServer$, PersonalizationService.OPENPOS_MANAGED_SERVER_PROPERTY, value => value === 'true')
        ).subscribe(() => {
            this.personalizationInitialized$.next(true);
        });
    }

    public personalizeFromSavedSession(): Observable<string>{
        const request = new PersonalizationRequest(this.deviceToken$.getValue(), null, null, null );
        return this.sendPersonalizationRequest(this.sslEnabled$.getValue(), this.serverName$.getValue(), this.serverPort$.getValue(), request, null);
    }

    public hasSavedSession(): boolean {
        return !!this.deviceToken$.getValue() && !!this.serverPort$.getValue() && !!this.serverName$.getValue();
    }

    public personalize(
        serverName: string,
        serverPort: string,
        deviceId: string,
        appId: string,
        personalizationProperties?: Map<string, string>,
        sslEnabled?: boolean): Observable<string> {

        let request = new PersonalizationRequest(this.deviceToken$.getValue(), deviceId, appId, null);
        return this.sendPersonalizationRequest( sslEnabled, serverName, serverPort, request, personalizationProperties);
    }

    public personalizeWithToken(
        serverName: string,
        serverPort: string,
        deviceToken: string,
        sslEnabled?: boolean
    ){
        let request = new PersonalizationRequest(deviceToken, null, null, null);
        return this.sendPersonalizationRequest( sslEnabled, serverName, serverPort, request, null);
    }

    private sendPersonalizationRequest( sslEnabled: boolean, serverName: string, serverPort: string, request: PersonalizationRequest, personalizationParameters: Map<string, string> ) : Observable<string> {
        let url = sslEnabled ? 'https://' : 'http://';
        url += serverName + ':' + serverPort + '/devices/personalize';

        if( personalizationParameters ){
            personalizationParameters.forEach( (value, key ) => request.personalizationParameters[key] = value);
        }

        return this.http.post<PersonalizationResponse>(url, request).pipe(
            map( (response: PersonalizationResponse) => {
                console.info(`personalizing with server: ${serverName}, port: ${serverPort}, deviceId: ${request.deviceId}`);
                this.setServerName(serverName);
                this.setServerPort(serverPort);
                this.setDeviceId(response.deviceModel.deviceId);
                this.setDeviceToken(response.authToken);
                this.setAppId(response.deviceModel.appId);
                if( !personalizationParameters ){
                    personalizationParameters = new Map<string, string>();
                }
                if(response.deviceModel.deviceParamModels){
                    response.deviceModel.deviceParamModels.forEach(value => personalizationParameters.set(value.paramName, value.paramValue));
                }

                this.setPersonalizationProperties(personalizationParameters);

                if (sslEnabled) {
                    this.setSslEnabled(sslEnabled);
                } else {
                    this.setSslEnabled(false);
                }

                this.personalizationSuccessFul$.next(true);
                return 'Personalization successful';
            }),
            catchError( error => {
                    this.personalizationSuccessFul$.next(false);
                    if(error.status == 401){
                        return throwError(`Device saved token does not match server`);
                    }

                    if(error.status == 0) {
                        return throwError(`Unable to connect to ${serverName}:${serverPort}`);
                    }

                    return throwError(`${error.statusText}`);

                })
        )
    }

    public dePersonalize() {
        PersonalizationService.setStorageValue('serverName', null);
        PersonalizationService.setStorageValue('serverPort', null);
        PersonalizationService.setStorageValue('deviceToken', null);
        PersonalizationService.setStorageValue('theme', null);
        PersonalizationService.setStorageValue('sslEnabled', null);
    }


    public requestPersonalizationConfig(serverName: string, serverPort: string, sslEnabled: boolean): Observable<PersonalizationConfigResponse> {
        let url = sslEnabled ? 'https://' : 'http://';
        url += serverName + ':' + serverPort + '/devices/personalizationConfig';

        console.log('Requesting Personalization config with url: ' + url);
        return this.http.get<PersonalizationConfigResponse>(url).pipe(
            tap( result =>  result ? console.log('Successful retrieval of Personalization Config with url: ' + url) : null )
        );
    }

    public getPersonalizationProperties$(): BehaviorSubject<Map<string, string>> {
        return this.personalizationProperties$;
    }

    public getSslEnabled$(): BehaviorSubject<boolean> {
        return this.sslEnabled$;
    }

    public getServerName$(): BehaviorSubject<string> {
        return this.serverName$;
    }

    public getServerPort$(): BehaviorSubject<string> {
        return this.serverPort$;
    }

    public getDeviceId$(): BehaviorSubject<string> {
        return this.deviceId$;
    }

    public getAppId$(): BehaviorSubject<string> {
        return this.appId$;
    }

    public getDeviceToken$(): BehaviorSubject<string> {
        return this.deviceToken$;
    }

    public getIsManagedServer$(): BehaviorSubject<boolean> {
        return this.isManagedServer$;
    }

    public getPersonalizationSuccessful$(): BehaviorSubject<boolean> {
        return this.personalizationSuccessFul$;
    }

    private setPersonalizationProperties(personalizationProperties?: Map<string, string>) {
        this.personalizationProperties$.next(personalizationProperties);
    }

    private setSslEnabled(enabled: boolean) {
        PersonalizationService.setStorageValue('sslEnabled', enabled + '');
        this.sslEnabled$.next(enabled);
    }

    private setServerName(name: string) {
        PersonalizationService.setStorageValue('serverName', name);
        this.serverName$.next(name);
    }

    private setServerPort(port: string) {
        PersonalizationService.setStorageValue('serverPort', port);
        this.serverPort$.next(port);
    }

    private setDeviceId(id: string) {
        this.deviceId$.next(id);
    }

    private setAppId(id: string) {
        this.appId$.next(id);
    }

    private setDeviceToken(token: string){
        PersonalizationService.setStorageValue('deviceToken', token);
        this.deviceToken$.next(token);
    }

    public refreshApp() {
        window.location.reload();
    }

    private static canUseCapacitorStorage(): boolean {
        // need to check if its native too because there is a default
        // web implementation that we don't want to use under web.
        return Capacitor.isNative && Capacitor.isPluginAvailable('Storage');
    }

    private static async updateStorageVariable(subject: Subject<any>, key: string, convert?: (value: string) => any) {
        let value: string;

        if (PersonalizationService.canUseCapacitorStorage()) {
            let result = await CapacitorPlugins.Storage.get({ key: key });

            if (!result) {
                return;
            }

            value = result.value;
        } else {
            value = localStorage.getItem(key);
        }

        let converted;

        if (convert) {
            converted = convert(value);
        } else {
            converted = value;
        }

        subject.next(converted);
    }

    private static async setStorageValue(key: string, value: string | null) {
        if (PersonalizationService.canUseCapacitorStorage()) {
            if (value === null) {
                await CapacitorPlugins.Storage.remove({ key: key });
            } else {
                await CapacitorPlugins.Storage.set({ key: key, value: value });
            }
        } else {
            if (value === null) {
                localStorage.removeItem(key);
            } else {
                localStorage.setItem(key, value);
            }
        }
    }
}
