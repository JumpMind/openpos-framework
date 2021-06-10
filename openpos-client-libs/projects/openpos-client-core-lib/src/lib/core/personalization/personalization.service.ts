import {Injectable, Injector} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PersonalizationConfigResponse} from './personalization-config-response.interface';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {catchError, map, tap, timeout} from 'rxjs/operators';
import {PersonalizationRequest} from './personalization-request';
import {PersonalizationResponse} from './personalization-response.interface';
import {AutoPersonalizationParametersResponse} from "./device-personalization.interface";
import {ZeroconfService} from "@ionic-native/zeroconf";
import {Configuration} from "../../configuration/configuration";
import {WrapperService} from "../services/wrapper.service";

@Injectable({
    providedIn: 'root'
})
export class PersonalizationService {
    static readonly OPENPOS_MANAGED_SERVER_PROPERTY = 'managedServer';

    private personalizationProperties$ = new BehaviorSubject<Map<string, string>>(null);
    private deviceId$ = new BehaviorSubject<string>(null);
    private appId$ = new BehaviorSubject<string>(null);
    private deviceToken$ = new BehaviorSubject<string>(localStorage.getItem('deviceToken'));
    private serverName$ = new BehaviorSubject<string>(localStorage.getItem('serverName'));
    private serverPort$ = new BehaviorSubject<string>(localStorage.getItem('serverPort'));
    private sslEnabled$ = new BehaviorSubject<boolean>('true' === localStorage.getItem('sslEnabled'));
    private isManagedServer$ = new BehaviorSubject<boolean>('true' === localStorage.getItem(PersonalizationService.OPENPOS_MANAGED_SERVER_PROPERTY));
    private personalizationSuccessFul$ = new BehaviorSubject<boolean>(false);

    constructor(private http: HttpClient, private wrapperService: WrapperService, private injector: Injector) {
    }

    public shouldAutoPersonalize(): boolean {
        return this.wrapperService.shouldAutoPersonalize();
    }

    public getAutoPersonalizationParameters(deviceName: string, url: string): Observable<AutoPersonalizationParametersResponse> {
        const protocol = this.sslEnabled$.getValue() ? 'https://' : 'http://';
        url = protocol + url;
        return this.http.get<AutoPersonalizationParametersResponse>(url, { params: { deviceName: deviceName }})
            .pipe(
                timeout(Configuration.autoPersonalizationRequestTimeoutMillis),
                tap(response => {
                    if (response) {
                        console.log('Auto personalization response received.', response);
                        response.sslEnabled = this.sslEnabled$.getValue();
                    }
                }));
    }

    public personalizeFromSavedSession(): Observable<string> {
        let request = new PersonalizationRequest(this.deviceToken$.getValue(), null, null, null);
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
        return this.sendPersonalizationRequest(sslEnabled, serverName, serverPort, request, personalizationProperties);

    }

    private sendPersonalizationRequest(sslEnabled: boolean, serverName: string, serverPort: string, request: PersonalizationRequest, personalizationParameters: Map<string, string>): Observable<string> {
        let url = sslEnabled ? 'https://' : 'http://';
        url += serverName + ':' + serverPort + '/devices/personalize';

        if (personalizationParameters) {
            personalizationParameters.forEach((value, key) => request.personalizationParameters[key] = value);
        }

        return this.http.post<PersonalizationResponse>(url, request).pipe(
            map((response: PersonalizationResponse) => {
                console.info(`personalizing with server: ${serverName}, port: ${serverPort}, deviceId: ${request.deviceId}`);
                this.setServerName(serverName);
                this.setServerPort(serverPort);
                this.setDeviceId(response.deviceModel.deviceId);
                this.setDeviceToken(response.authToken);
                this.setAppId(response.deviceModel.appId);
                if (!personalizationParameters) {
                    personalizationParameters = new Map<string, string>();
                }
                if (response.deviceModel.deviceParamModels) {
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
            catchError(error => {
                this.personalizationSuccessFul$.next(false);
                if (error.status == 401) {
                    return throwError(`Device saved token does not match server`);
                }

                if (error.status == 0) {
                    return throwError(`Unable to connect to ${serverName}:${serverPort}`);
                }

                return throwError(`${error.statusText}`);

            })
        )
    }

    public dePersonalize() {
        localStorage.removeItem('serverName');
        localStorage.removeItem('serverPort');
        localStorage.removeItem('deviceToken');
        localStorage.removeItem('theme');
        localStorage.removeItem('sslEnabled');
    }


    public requestPersonalizationConfig(serverName: string, serverPort: string, sslEnabled: boolean): Observable<PersonalizationConfigResponse> {
        let url = sslEnabled ? 'https://' : 'http://';
        url += serverName + ':' + serverPort + '/devices/personalizationConfig';

        console.log('Requesting Personalization config with url: ' + url);
        return this.http.get<PersonalizationConfigResponse>(url).pipe(
            tap(result => result ? console.log('Successful retrieval of Personalization Config with url: ' + url) : null)
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
        localStorage.setItem('sslEnabled', enabled + '');
        this.sslEnabled$.next(enabled);
    }

    private setServerName(name: string) {
        localStorage.setItem('serverName', name);
        this.serverName$.next(name);
    }

    private setServerPort(port: string) {
        localStorage.setItem('serverPort', port);
        this.serverPort$.next(port);
    }

    private setDeviceId(id: string) {
        this.deviceId$.next(id);
    }

    private setAppId(id: string) {
        this.appId$.next(id);
    }

    private setDeviceToken(token: string) {
        localStorage.setItem('deviceToken', token);
        this.deviceToken$.next(token);
    }

    public refreshApp() {
        window.location.reload();
    }
}


