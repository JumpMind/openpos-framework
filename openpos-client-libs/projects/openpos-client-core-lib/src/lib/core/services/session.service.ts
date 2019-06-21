import { VERSION } from './../../version';
import { ILoading } from './../interfaces/loading.interface';
import { Logger } from './logger.service';

import { Configuration } from './../../configuration/configuration';
import { IMessageHandler } from './../interfaces/message-handler.interface';
import { PersonalizationService } from './personalization.service';

import { Observable, Subscription, BehaviorSubject, Subject, merge } from 'rxjs';
import { map, filter } from 'rxjs/operators';
import { Message } from '@stomp/stompjs';
import { Injectable, NgZone, } from '@angular/core';
import { StompState, StompRService } from '@stomp/ng2-stompjs';
import { MatDialog } from '@angular/material';
import { ActionIntercepter } from '../action-intercepter';
// Importing the ../components barrel causes a circular reference since dynamic-screen references back to here,
// so we will import those files directly
import { LoaderState } from '../components/loader/loader-state';
import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import { IDeviceResponse } from '../oldplugins/device-response.interface';
import { InAppBrowserPlugin } from '../oldplugins/in-app-browser.plugin';
import { IActionItem } from '../interfaces/action-item.interface';
import { IUrlMenuItem } from '../interfaces/url-menu-item.interface';
import { IConfirmationDialog } from '../interfaces/confirmation-dialog.interface';
import { OldPluginService } from './old-plugin.service';
import { AppInjector } from '../app-injector';
import { HttpClient } from '@angular/common/http';
import { PingParams } from '../interfaces/ping-params.interface';
import { PingResult } from '../interfaces/ping-result.interface';
import { PersonalizationResponse } from '../interfaces/personalization-response.interface';
import { ElectronService } from 'ngx-electron';

declare var window: any;
export class QueueLoadingMessage implements ILoading {
    type = 'Loading';
    title: string;
    queue = true;
    cancel = false;

    constructor(text: string) {
        this.title = text;
    }
}

export class ImmediateLoadingMessage implements ILoading {
    type = 'Loading';
    title: string;
    queue = false;
    cancel = false;

    constructor(text: string) {
        this.title = text;
    }
}

export class CancelLoadingMessage implements ILoading {
    type = 'Loading';
    cancel = true;
    queue = false;
}

export class ConnectedMessage {
    type = 'Connected';
}

@Injectable({
    providedIn: 'root',
})
export class SessionService implements IMessageHandler<any> {

    public state: Observable<string>;

    private appId: string;

    private subscription: Subscription;

    private authToken: string;

    private stompDebug = false;

    private actionPayloads: Map<string, () => void> = new Map<string, () => void>();

    private actionIntercepters: Map<string, ActionIntercepter> = new Map();

    private waitingForResponse = false;
    private actionDisablers = new Map<string, BehaviorSubject<boolean>>();

    public inBackground = false;

    private stompStateSubscription: Subscription;

    public onServerConnect: BehaviorSubject<boolean>;

    private stompJsonMessages$ = new BehaviorSubject<any>(false);

    private sessionMessages$ = new Subject<any>();

    private disconnectedMessage = LoaderState.DISCONNECTED_TITLE;

    private queryParams = new Map();

    private deletedLaunchFlg = false;

    constructor(
        private log: Logger,
        private stompService: StompRService,
        public dialogService: MatDialog,
        public zone: NgZone,
        protected personalization: PersonalizationService,
        private http: HttpClient,
        private electron: ElectronService
    ) {
        this.zone.onError.subscribe((e) => {
            console.error(`[OpenPOS]${e}`);
        });
        this.onServerConnect = new BehaviorSubject<boolean>(false);

        this.registerMessageHandler(this);
    }

    public sendMessage(message: any) {
        this.sessionMessages$.next(message);
    }

    public getMessages(...types: string[]): Observable<any> {
        return merge(
            this.stompJsonMessages$,
            this.sessionMessages$).pipe(filter(s => types && types.length > 0 ? types.includes(s.type) : true));
    }

    public registerMessageHandler(handler: IMessageHandler<any>, ...types: string[]): Subscription {
        return merge(
            this.stompJsonMessages$,
            this.sessionMessages$).pipe(filter(s => types && types.length > 0 ? types.includes(s.type) : true)).
            subscribe(s => this.zone.run(() => handler.handle(s)));
    }

    public isRunningInBrowser(): boolean {
        const app = document.URL.indexOf('http://') === -1 && document.URL.indexOf('https://') === -1;
        return !app;
    }

    private buildTopicName(): string {
        return '/topic/app/' + this.appId + '/node/' + this.personalization.getDeviceId();
    }

    public setAuthToken(token: string) {
        this.authToken = token;
    }

    public addQueryParam(key: string, value: string) {
        this.queryParams[key] = value;
    }

    public setAppId(value: string) {
        this.appId = value;
    }

    public getAppId(): string {
        return this.appId;
    }

    public connected(): boolean {
        return this.stompService && this.stompService.connected();
    }
    private appendPersonalizationProperties(headers: any) {
        const personalizationProperties = this.personalization.getPersonalizationProperties();
        if (personalizationProperties && headers) {
            const keys = Array.from(personalizationProperties.keys());
            for (const key of keys) {
                headers[key] = personalizationProperties.get(key);
            }
        }
    }

    /*
     * Need to come up with a better way to enapsulate electron and node ... should put these reference behind our new platform interface
     */
    private deleteLaunchingFlg() {
        const fs = this.electron.isElectronApp ? this.electron.remote.require('fs') : window.fs;
        const launchingFile = 'launching.flg';
        this.log.info('node.js fs exists? ' + fs);
        this.log.info('launching.flg file exists? ' + (fs && fs.existsSync(launchingFile)));
        if (fs && fs.existsSync(launchingFile)) {
            fs.unlink(launchingFile, (err) => {
                if (err) {
                    this.log.info('unable to remove ' + launchingFile);
                } else {
                    this.log.info(launchingFile + ' was removed');
                }
            });
        }
    }

    private getHeaders(): any {
        const headers = {
            authToken: this.authToken,
            compatibilityVersion: Configuration.compatibilityVersion,
            appId: this.appId,
            deviceId: this.personalization.getDeviceId(),
            queryParams: JSON.stringify(this.queryParams),
            version: JSON.stringify(VERSION)
        };
        this.appendPersonalizationProperties(headers);
        return headers;
    }

    public subscribe() {
        if (this.subscription) {
            return;
        }

        const url = this.personalization.getWebsocketUrl();
        this.log.info('creating new stomp service at: ' + url);

        this.stompService.config = {
            url,
            headers: this.getHeaders(),
            heartbeat_in: 0, // Typical value 0 - disabled
            heartbeat_out: 20000, // Typical value 20000 - every 20 seconds
            reconnect_delay: 250,  // Typical value is 5000, 0 disables.
            debug: this.stompDebug
        };

        this.stompService.initAndConnect();

        const currentTopic = this.buildTopicName();

        this.log.info('subscribing to server at: ' + currentTopic);

        const messages: Observable<Message> = this.stompService.subscribe(currentTopic);

        this.subscription = messages.subscribe((message: Message) => {
            this.log.info('Got STOMP message');
            if (this.inBackground) {
                this.log.info('Leaving background');
                this.inBackground = false;
            }
            if (this.isMessageVersionValid(message)) {
                const json = JSON.parse(message.body);
                this.logStompJson(json);
                this.stompJsonMessages$.next(json);
            } else {
                this.log.info(`Showing incompatible version screen`);
                this.stompJsonMessages$.next(this.buildIncompatibleVersionScreen());
            }
        });

        this.state = this.stompService.state.pipe(map((state: number) => StompState[state]));

        if (!this.stompStateSubscription) {
            this.stompStateSubscription = this.state.subscribe(stompState => {
                if (stompState === 'CONNECTED') {
                    this.log.info('STOMP connecting');
                    if (!this.onServerConnect.value) {
                        this.onServerConnect.next(true);
                    }
                    this.sendMessage(new ConnectedMessage());
                    this.cancelLoading();
                } else if (stompState === 'DISCONNECTING') {
                    this.log.info('STOMP disconnecting');
                } else if (stompState === 'CLOSED') {
                    this.log.info('STOMP closed');
                    this.sendDisconnected();
                }
            });
        }

        if (!this.connected()) {
            this.sendDisconnected();
        }

    }

    private sendDisconnected() {
        this.sendMessage(new ImmediateLoadingMessage(this.disconnectedMessage));
    }

    handle(message: any) {
        if (!this.deletedLaunchFlg && message && message.type === 'ConfigChanged') {
            this.deleteLaunchingFlg();
            this.deletedLaunchFlg = true;
        }
    }

    private logStompJson(json: any) {
        if (json && json.sequenceNumber && json.screenType) {
            this.log.info(`[logStompJson] type: ${json.type}, screenType: ${json.screenType}, seqNo: ${json.sequenceNumber}`);
        } else if (json) {
            this.log.info(`[logStompJson] type: ${json.type}`);
        } else {
            this.log.info(`[logStompJson] ${json}`);
        }
    }

    private buildIncompatibleVersionScreen(): any {
        return {
            type: 'Dialog',
            screenType: 'Dialog',
            template: { dialog: true, type: 'BlankWithBar' },
            dialogProperties: { closeable: false },
            title: 'Incompatible Versions',
            message: Configuration.incompatibleVersionMessage.split('\n')
        };
    }

    private isMessageVersionValid(message: Message): boolean {
        const valid = message.headers.compatibilityVersion === Configuration.compatibilityVersion;
        if (!valid) {
            this.log.info(`INCOMPATIBLE VERSIONS. Client compatibilityVersion: ${Configuration.compatibilityVersion}, ` +
                `server compatibilityVersion: ${message.headers.compatibilityVersion}`);
        }
        return valid;
    }

    public async ping(pingParams?: PingParams): Promise<PingResult> {
        let url = '';
        if (pingParams) {
            let protocol = 'http://';
            if (pingParams.useSsl) {
                protocol = 'https://';
            }
            url = protocol + pingParams.serverName;
            if (pingParams.serverPort) {
                url = url + ':' + pingParams.serverPort;
            }
            url = url + '/ping';

        } else {
            url = `${this.personalization.getServerBaseURL()}/ping`;
        }

        this.log.info('testing url: ' + url);

        let pingError: any = null;
        try {
            const httpResult = await this.http.get(url, {}).toPromise();
            if (httpResult) {
                this.log.info('successful validation of ' + url);
                return { success: true };
            } else {
                pingError = { message: '?' };
            }
        } catch (error) {
            pingError = error;
        }

        if (pingError) {
            this.log.info('bad validation of ' + url + ' with an error message of :' + pingError.message);
            return { success: false, message: pingError.message };
        }
    }

    public async requestPersonalization(pingParams?: PingParams): Promise<PersonalizationResponse> {
        let url = '';
        if (pingParams) {
            let protocol = 'http://';
            if (pingParams.useSsl) {
                protocol = 'https://';
            }
            url = protocol + pingParams.serverName;
            if (pingParams.serverPort) {
                url = url + ':' + pingParams.serverPort;
            }
            url = url + '/personalize';

        } else {
            url = `${this.personalization.getServerBaseURL()}/personalize`;
        }

        this.log.info('Requesting Personalization with url: ' + url);

        let personalizeError: any = null;
        try {
            const httpResult = await this.http.get<PersonalizationResponse>(url, {}).toPromise();
            if (httpResult) {
                httpResult.success = true;
                this.log.info('Successful Personalization with url: ' + url);
                return httpResult;
            } else {
                personalizeError = { message: '?' };
            }
        } catch (error) {
            personalizeError = error;
        }

        if (personalizeError) {
            this.log.info('bad validation of ' + url + ' with an error message of :' + personalizeError.message);
            return { success: false, message: personalizeError.message };
        }
    }

    public unsubscribe() {
        if (!this.subscription) {
            return;
        }

        this.log.info('unsubscribing from stomp service ...');

        // This will internally unsubscribe from Stomp Broker
        // There are two subscriptions - one created explicitly, the other created in the template by use of 'async'
        this.subscription.unsubscribe();
        this.subscription = null;

        this.log.info('disconnecting from stomp service');
        this.stompService.disconnect();
    }

    public onDeviceResponse(deviceResponse: IDeviceResponse) {
        const sendResponseBackToServer = () => {
            // tslint:disable-next-line:max-line-length
            this.log.info(`>>> Publish deviceResponse requestId: "${deviceResponse.requestId}" deviceId: ${deviceResponse.deviceId} type: ${deviceResponse.type}`);
            this.stompService.publish(
                `/app/device/app/${this.appId}/node/${this.personalization.getDeviceId()}/device/${deviceResponse.deviceId}`,
                JSON.stringify(deviceResponse));
        };

        // see if we have any intercepters registered for the type of this deviceResponse
        // otherwise just send the response
        if (this.actionIntercepters.has(deviceResponse.type)) {
            this.actionIntercepters.get(deviceResponse.type).intercept(deviceResponse, sendResponseBackToServer);
        } else {
            sendResponseBackToServer();
        }
    }

    public async onValueChange(action: string, payload?: any) {
        this.onAction(action, payload, null, true);
    }

    public async onAction(action: string | IActionItem,
                          payload?: any, confirm?: string | IConfirmationDialog, isValueChangedAction?: boolean) {
        if (action) {
            let response: any = null;
            let actionString = '';
            // we need to figure out if we are a menuItem or just a string
            if (action.hasOwnProperty('action')) {
                const menuItem = action as IActionItem;
                confirm = menuItem.confirmationDialog;
                actionString = menuItem.action;
                // check to see if we are an IURLMenuItem
                if (menuItem.hasOwnProperty('url')) {
                    const urlMenuItem = menuItem as IUrlMenuItem;
                    // tslint:disable-next-line:max-line-length
                    this.log.info(`About to open: ${urlMenuItem.url} in target mode: ${urlMenuItem.targetMode}, with options: ${urlMenuItem.options}`);
                    const pluginService = AppInjector.Instance.get(OldPluginService);
                    // Use inAppBrowserPlugin when available since it tracks whether or not the browser is active.
                    pluginService.getPlugin('InAppBrowser').then(plugin => {
                        const inAppPlugin = plugin as InAppBrowserPlugin;
                        inAppPlugin.open(urlMenuItem.url, urlMenuItem.targetMode, urlMenuItem.options);
                    }).catch(error => {
                        this.log.info(`InAppBrowser not found, using window.open. Reason: ${error}`);
                        window.open(urlMenuItem.url, urlMenuItem.targetMode, urlMenuItem.options);
                    });
                    if (!actionString || 0 === actionString.length) {
                        return;
                    }
                }
            } else {
                actionString = action as string;
            }

            this.log.info(`action is: ${actionString}`);

            if (confirm) {
                this.log.info('Confirming action');
                let confirmD: IConfirmationDialog;
                if (confirm.hasOwnProperty('message')) {
                    confirmD = confirm as IConfirmationDialog;
                } else {
                    confirmD = {
                        title: '', message: confirm as string, cancelButtonName: 'No',
                        confirmButtonName: 'Yes', cancelAction: null, confirmAction: null
                    };
                }
                const dialogRef = this.dialogService.open(ConfirmationDialogComponent, { disableClose: true });
                dialogRef.componentInstance.confirmDialog = confirmD;
                const result = await dialogRef.afterClosed().toPromise();

                // if we didn't confirm return and don't send the action to the server
                if (!result) {
                    this.log.info('Canceling action');
                    return;
                }
            }

            let processAction = true;

            // First we will use the payload passed into this function then
            // Check if we have registered action payload
            // Otherwise we will send whatever is in this.response
            if (payload != null) {
                response = payload;
            } else if (this.actionPayloads.has(actionString)) {
                this.log.info(`Checking registered action payload for ${actionString}`);
                try {
                    response = this.actionPayloads.get(actionString)();
                } catch (e) {
                    this.log.info(`invalid action payload for ${actionString}: ` + e);
                    processAction = false;
                }
            }

            if (processAction && !this.waitingForResponse) {
                const sendToServer = () => {
                    this.log.info(`>>> Post action "${actionString}"`);
                    if (!isValueChangedAction) {
                        this.queueLoading();
                    }
                    if (!this.publish(actionString, 'Screen', response)) {
                        this.cancelLoading();
                    }
                };

                // see if we have any intercepters registered
                // otherwise just send the action
                if (this.actionIntercepters.has(actionString)) {
                    const interceptor = this.actionIntercepters.get(actionString);
                    interceptor.intercept(response, sendToServer);
                    if (interceptor.options && interceptor.options.showLoadingAfterIntercept) {
                        if (!isValueChangedAction) {
                            this.queueLoading();
                        }
                    }
                } else {
                    sendToServer();
                }
            } else {
                this.log.info(
                    `Not sending action: ${actionString}.  processAction: ${processAction}, waitingForResponse:${this.waitingForResponse}`);
            }

        } else {
            this.log.info(`received an invalid action: ${action}`);
        }
    }

    public keepAlive() {
        if (this.subscription) {
            this.log.info(`>>> KeepAlive`);
            this.publish('KeepAlive', 'KeepAlive');
        }
    }

    public setDisconnectedMessage(message: string) {
        if (message) {
            this.disconnectedMessage = message;
        } else {
            this.disconnectedMessage = LoaderState.DISCONNECTED_TITLE;
        }
    }

    public refreshScreen() {
        this.publish('Refresh', 'Screen');
    }

    public publish(actionString: string, type: string, payload?: any): boolean {
        // Block any actions if we are backgrounded and running in cordova
        // (unless we are coming back out of the background)
        if (this.inBackground && actionString !== 'Refresh') {
            this.log.info(`Blocked action '${actionString}' because app is in background.`);
            return false;
        }
        const deviceId = this.personalization.getDeviceId();
        if (this.appId && deviceId) {
            this.log.info(`Publishing action '${actionString}' of type '${type}' to server...`);
            this.stompService.publish('/app/action/app/' + this.appId + '/node/' + deviceId,
                JSON.stringify({ name: actionString, type, data: payload }));
            return true;
        } else {
            this.log.info(`Can't publish action '${actionString}' of type '${type}' ` +
                `due to undefined App ID (${this.appId}) or Device ID (${deviceId})`);
            return false;
        }
    }

    private queueLoading() {
        this.waitingForResponse = true;
        this.sendMessage(new QueueLoadingMessage(LoaderState.LOADING_TITLE));
    }

    public cancelLoading() {
        this.waitingForResponse = false;
        this.sendMessage(new CancelLoadingMessage());
    }

    public registerActionPayload(actionName: string, actionValue: () => void) {
        this.actionPayloads.set(actionName, actionValue);
    }

    public unregisterActionPayloads() {
        this.actionPayloads.clear();
    }

    public unregisterActionPayload(actionName: string) {
        this.actionPayloads.delete(actionName);
    }

    public registerActionIntercepter(actionName: string, actionIntercepter: ActionIntercepter) {
        this.actionIntercepters.set(actionName, actionIntercepter);
    }

    public unregisterActionIntercepters() {
        this.actionIntercepters.clear();
    }

    public unregisterActionIntercepter(actionName: string) {
        this.actionIntercepters.delete(actionName);
    }


    public registerActionDisabler(action: string, disabler: Observable<boolean>): Subscription {
        if (!this.actionDisablers.has(action)) {
            this.actionDisablers.set(action, new BehaviorSubject<boolean>(false));
        }

        return disabler.subscribe(value => this.actionDisablers.get(action).next(value));
    }

    public actionIsDisabled(action: string): Observable<boolean> {
        if (!this.actionDisablers.has(action)) {
            this.actionDisablers.set(action, new BehaviorSubject<boolean>(false));
        }

        return this.actionDisablers.get(action);
    }

    public getCurrencyDenomination(): string {
        return 'USD';
    }

    public getApiServerBaseURL(): string {
        return `${this.personalization.getServerBaseURL()}/api`;
    }

}
