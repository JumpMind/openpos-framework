import { VERSION } from './../../version';
import { ILoading } from './../interfaces/loading.interface';

import { Configuration } from './../../configuration/configuration';
import { IMessageHandler } from './../interfaces/message-handler.interface';
import { PersonalizationService } from '../personalization/personalization.service';

import { Observable, Subscription, BehaviorSubject, Subject, merge, timer, ConnectableObservable } from 'rxjs';
import { map, filter, takeWhile, publishReplay } from 'rxjs/operators';
import { Message } from '@stomp/stompjs';
import { Injectable, NgZone, Inject, } from '@angular/core';
import { StompState, StompRService } from '@stomp/ng2-stompjs';
import { MatDialog } from '@angular/material';
// Importing the ../components barrel causes a circular reference since dynamic-screen references back to here,
// so we will import those files directly
import { LoaderState } from '../../shared/components/loader/loader-state';
import { IDeviceResponse } from '../oldplugins/device-response.interface';
import { HttpClient } from '@angular/common/http';
import { PingParams } from '../interfaces/ping-params.interface';
import { PingResult } from '../interfaces/ping-result.interface';
import { ElectronService } from 'ngx-electron';
import { OpenposMessage } from '../messages/message';
import { MessageTypes } from '../messages/message-types';
import { ActionMessage } from '../messages/action-message';
import { CLIENTCONTEXT, IClientContext } from '../client-context/client-context-provider.interface';
import { DiscoveryService } from '../discovery/discovery.service';
import { UnlockScreenMessage } from '../messages/unlock-screen-message';
import { SplashScreen } from '../messages/splash-screen-message';

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
    type = MessageTypes.CONNECTED;
}

@Injectable({
    providedIn: 'root',
})
// Works around problem with re-establishing a STOMP connection
// as outlined here: https://github.com/stomp-js/ng2-stompjs/issues/58
export class OpenposStompService extends StompRService {
    disconnect() {
      if (this.client) {
        this.client.reconnect_delay = 0;
      }
      super.disconnect();
    }
}

@Injectable({
    providedIn: 'root',
})
export class SessionService implements IMessageHandler<any> {

    public state: Observable<string>;

    private subscription: Subscription;

    private authToken: string;

    private stompDebug = false;

    public inBackground = false;

    private stompStateSubscription: Subscription;

    public onServerConnect: BehaviorSubject<boolean>;

    private stompJsonMessages$ = new BehaviorSubject<any>(false);

    private sessionMessages$ = new Subject<any>();

    private disconnectedMessage = LoaderState.DISCONNECTED_TITLE;

    private queryParams = new Map();

    private deletedLaunchFlg = false;

    private reconnecting = false;

    private reconnectTimerSub: Subscription;
    private connectedOnce = false;

    public screenMessage$: Observable<any>;


    constructor(
        public dialogService: MatDialog,
        public zone: NgZone,
        protected stompService: OpenposStompService,
        protected personalization: PersonalizationService,
        protected discovery: DiscoveryService,
        private http: HttpClient,
        private electron: ElectronService,
        @Inject(CLIENTCONTEXT) private clientContexts: Array<IClientContext>
    ) {
        this.zone.onError.subscribe((e) => {
            if (typeof(e) === 'string') {
                console.error(`[OpenPOS] ${e}`);
            } else if(e.message) {
                console.error(`[OpenPOS] ${e.message}`, e);
            } else {
                console.error(`[OpenPOS] unexpected zone error`, e);
            }
        });
        this.onServerConnect = new BehaviorSubject<boolean>(false);

        this.registerMessageHandler(this);

        const screenMessagesBehavior = this.stompJsonMessages$.pipe(
            filter(message => message.type === MessageTypes.SCREEN && message.screenType !== 'NoOp'),
            publishReplay(1)
        ) as ConnectableObservable<any>;

        this.screenMessage$ = screenMessagesBehavior;

        // We need to capture incoming screen messages even with no subscribers, so make this hot 🔥
        screenMessagesBehavior.connect();
    }

    public sendMessage<T extends OpenposMessage>(message: T) {
        if ( message.type === MessageTypes.ACTION && message instanceof ActionMessage ) {
            const actionMessage = message as ActionMessage;
            this.publish(actionMessage.actionName, MessageTypes.SCREEN, actionMessage.payload, actionMessage.doNotBlockForResponse);
        } else if (message.type === MessageTypes.PROXY && message instanceof ActionMessage) {
            const actionMessage = message as ActionMessage;
            this.publish(actionMessage.actionName, actionMessage.type, actionMessage.payload, actionMessage.doNotBlockForResponse);
        }
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
        return '/topic/app/device/' + this.personalization.getDeviceId$().getValue();
    }

    public setAuthToken(token: string) {
        this.authToken = token;
    }

    public addQueryParam(key: string, value: string) {
        this.queryParams[key] = value;
    }

    public connected(): boolean {
        return this.stompService && this.stompService.connected();
    }

    private appendPersonalizationProperties(headers: any) {
        const personalizationProperties = this.personalization.getPersonalizationProperties$().getValue();
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
        console.info('node.js fs exists? ' + fs);
        console.info('launching.flg file exists? ' + (fs && fs.existsSync(launchingFile)));
        if (fs && fs.existsSync(launchingFile)) {
            fs.unlink(launchingFile, (err) => {
                if (err) {
                    console.info('unable to remove ' + launchingFile);
                } else {
                    console.info(launchingFile + ' was removed');
                }
            });
        }
    }

    private getHeaders(): any {
        const headers = {
            authToken: this.authToken,
            deviceToken: this.personalization.getDeviceToken$().getValue(),
            compatibilityVersion: Configuration.compatibilityVersion,
            appId: this.personalization.getAppId$().getValue(),
            deviceId: this.personalization.getDeviceId$().getValue(),
            queryParams: JSON.stringify(this.queryParams),
            version: JSON.stringify(VERSION)
        };
        this.appendPersonalizationProperties(headers);
        this.clientContexts.forEach( context => {
            const contextsToAdd = context.getContextProperties();
            contextsToAdd.forEach( (value, key ) => {
                headers[key] = value;
            });
        });
        return headers;
    }

    public async subscribe() {
        if (this.subscription) {
            return;
        }

        console.info(`Initiating session subscribe...`);
        const url: string = await this.negotiateWebsocketUrl();
        if (url) {
            console.info('creating new stomp service at: ' + url);

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

            console.info('subscribing to server at: ' + currentTopic);

            const messages: Observable<Message> = this.stompService.subscribe(currentTopic);

            this.subscription = messages.subscribe((message: Message) => {
                console.info('Got STOMP message');
                if (this.inBackground) {
                    console.info('Leaving background');
                    this.inBackground = false;
                }
                if (this.isMessageVersionValid(message)) {
                    const json = JSON.parse(message.body);
                    this.logStompJson(json);
                    this.stompJsonMessages$.next(json);
                } else {
                    console.info(`Showing incompatible version screen`);
                    this.stompJsonMessages$.next(this.buildIncompatibleVersionScreen());
                }
            });

            this.state = this.stompService.state.pipe(map((state: number) => StompState[state]));

            if (!this.stompStateSubscription) {
                this.stompStateSubscription = this.state.subscribe(stompState => {
                    this.handleStompState(stompState);
                });
            }
        } else {
            console.error('Failed to negotiate server url');
        }

        if (!this.connected()) {
            this.sendDisconnected();
        }

    }

    private handleStompState(stompState: string) {
        if (stompState === 'CONNECTED') {
            this.reconnecting = false;
            this.connectedOnce = true;
            console.info('STOMP connecting');
            if (!this.onServerConnect.value) {
                this.onServerConnect.next(true);
            }
            this.sendMessage(new ConnectedMessage());
            this.cancelLoading();
        } else if (stompState === 'DISCONNECTING') {
            console.info('STOMP disconnecting');
        } else if (stompState === 'CLOSED') {
            console.info('STOMP closed');
            this.sendMessage(new SplashScreen('Reconnecting to server...'));
            this.sendMessage(new UnlockScreenMessage());
            this.sendDisconnected();
            if (!this.reconnecting) {
                this.renegotiateConnection();
            }
        }
    }

    private async negotiateWebsocketUrl(): Promise<string> {
        if (this.personalization.getIsManagedServer$().getValue()) {
            if (! this.discovery.getWebsocketUrl()) {
                const discoverResp = await this.discovery.discoverDeviceProcess();
                if (!discoverResp || ! discoverResp.success) {
                    console.error(`Failed to get websocket url from OpenPOS Management Server. Reason: ` +
                        `${!!discoverResp ? discoverResp.message : 'unknown'}`);
                    return null;
                }
            }
        }
        return this.discovery.getWebsocketUrl();
    }

    private sendDisconnected() {
        if (this.connectedOnce) {
            this.sendMessage(new ImmediateLoadingMessage(this.disconnectedMessage));
        }
    }

    handle(message: any) {
        if (!this.deletedLaunchFlg && message && message.type === 'ConfigChanged') {
            this.deleteLaunchingFlg();
            this.deletedLaunchFlg = true;
        }
    }

    private logStompJson(json: any) {
        if (json && json.sequenceNumber && json.screenType) {
            console.info(`[logStompJson] type: ${json.type}, screenType: ${json.screenType}, seqNo: ${json.sequenceNumber}`);
        } else if (json) {
            console.info(`[logStompJson] type: ${json.type}`);
        } else {
            console.info(`[logStompJson] ${json}`);
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
            console.info(`INCOMPATIBLE VERSIONS. Client compatibilityVersion: ${Configuration.compatibilityVersion}, ` +
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
            url = `${this.discovery.getServerBaseURL()}/ping`;
        }

        console.info('testing url: ' + url);

        let pingError: any = null;
        try {
            const httpResult = await this.http.get(url, {}).toPromise();
            if (httpResult) {
                console.info('successful validation of ' + url);
                return { success: true };
            } else {
                pingError = { message: '?' };
            }
        } catch (error) {
            pingError = error;
        }

        if (pingError) {
            console.info('bad validation of ' + url + ' with an error message of :' + pingError.message);
            return { success: false, message: pingError.message };
        }
    }

    private async renegotiateConnection() {
        if (this.reconnecting) {
            return;
        }
        if (this.personalization.getIsManagedServer$().getValue()) {
            this.unsubscribe();
            this.reconnecting = true;
            this.reconnectTimerSub = timer(5000, 5000).pipe(takeWhile(() => this.reconnecting)).subscribe(async () => {
                if (await this.discovery.isManagementServerAlive()) {
                    console.debug(`Management server is alive`);
                    if (this.discovery.getWebsocketUrl()) {
                        // TODO: May not be necessary to run in zone, check.
                        this.zone.run(() => {
                            this.subscribe();
                            this.reconnectTimerSub.unsubscribe();
                        });
                    }
                } else {
                    console.debug(`Management server is not alive`);
                }
            } );
        }
    }

    public unsubscribe() {
        if (!this.subscription) {
            return;
        }

        console.info('unsubscribing from stomp service ...');

        // This will internally unsubscribe from Stomp Broker
        // There are two subscriptions - one created explicitly, the other created in the template by use of 'async'
        this.subscription.unsubscribe();
        this.subscription = null;

        this.stompStateSubscription.unsubscribe();
        this.stompStateSubscription = null;

        console.info('disconnecting from stomp service');
        this.stompService.disconnect();
        this.stompService.config = null;
    }

    public onDeviceResponse(deviceResponse: IDeviceResponse) {
        const sendResponseBackToServer = () => {
            // tslint:disable-next-line:max-line-length
            console.info(`>>> Publish deviceResponse requestId: "${deviceResponse.requestId}" deviceId: ${deviceResponse.deviceId} type: ${deviceResponse.type}`);
            this.stompService.publish(
                `/app/device/device/${this.personalization.getDeviceId$().getValue()}`,
                JSON.stringify(deviceResponse));
        };

        sendResponseBackToServer();
    }

    public keepAlive() {
        if (this.subscription) {
            console.info(`>>> KeepAlive`);
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

    public publish(actionString: string, type: string, payload?: any, doNotBlockForResponse = false): boolean {
        // Block any actions if we are backgrounded and running in cordova
        // (unless we are coming back out of the background)
        if (this.inBackground && actionString !== 'Refresh') {
            console.info(`Blocked action '${actionString}' because app is in background.`);
            return false;
        }
        const deviceId = this.personalization.getDeviceId$().getValue();
        if (deviceId) {
            console.info(`Publishing action '${actionString}' of type '${type}' to server...`);
            this.stompService.publish('/app/action/device/' + deviceId,
                JSON.stringify({ name: actionString, type, data: payload, doNotBlockForResponse: doNotBlockForResponse }));
            return true;
        } else {
            console.info(`Can't publish action '${actionString}' of type '${type}' ` +
                `due to undefined Device ID (${deviceId})`);
            return false;
        }
    }

    public cancelLoading() {
        this.sendMessage(new CancelLoadingMessage());
    }

    public getCurrencyDenomination(): string {
        return 'USD';
    }
}
