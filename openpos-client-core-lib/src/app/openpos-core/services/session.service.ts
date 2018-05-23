import { LoaderState } from './../common/loader/loader-state';
import { IDeviceResponse } from './../common/ideviceresponse';
import { IDeviceRequest } from './../common/idevicerequest';
import { IMenuItem } from '../common/imenuitem';
import { IDialog } from '../common/idialog';
import { Observable } from 'rxjs/Observable';
import { Message } from '@stomp/stompjs';
import { Subscription } from 'rxjs/Subscription';
import { Injectable, EventEmitter, NgZone } from '@angular/core';
import { StompService, StompState } from '@stomp/ng2-stompjs';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { Scan } from '../common/scan';
import { FunctionActionIntercepter, ActionIntercepter } from '../common/action-intercepter';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ConfirmationDialogComponent } from '../common/confirmation-dialog/confirmation-dialog.component';
import { IUrlMenuItem } from '../common/iurlmenuitem';
import { DEFAULT_LOCALE, ILocaleService } from './locale.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class SessionService implements ILocaleService {

  private screen: any;

  public dialog: any;

  public state: Observable<string>;

  public response: any;

  private appId: String;

  private deviceName: string;

  private subscribed: boolean;

  private subscription: any;

  private messages: Observable<Message>;

  public onDeviceRequest = new EventEmitter<IDeviceRequest>();

  private screenSource = new BehaviorSubject<any>(null);

  private dialogSource = new BehaviorSubject<any>(null);

  private loading: boolean;

  private stompService: StompService;

  private stompDebug = false;

  private actionPayloads: Map<string, Function> = new Map<string, Function>();

  private actionIntercepters: Map<string, ActionIntercepter> = new Map();

  private serverBaseUrl: string;

  loaderState: LoaderState;

  constructor(private location: Location, private router: Router, public dialogService: MatDialog,
    public zone: NgZone) {
    this.loaderState = new LoaderState(this);
    this.zone.onError.subscribe((e) => {
      console.error(`[OpenPOS]${e}`);
    });
  }

  public subscribeForScreenUpdates(callback: (screen: any) => any): Subscription {
    return this.screenSource.asObservable().subscribe(
      screen => this.zone.run(() => callback(screen))
    );
  }

  public subscribeForDialogUpdates(callback: (dialog: any) => any): Subscription {
    return this.dialogSource.asObservable().subscribe(
      dialog => this.zone.run(() => callback(dialog))
    );
  }

  public isRunningInBrowser(): boolean {
    const app = document.URL.indexOf('http://') === -1 && document.URL.indexOf('https://') === -1;
    return !app;
  }

  public personalize(serverName: string, serverPort: string, storeId: string, deviceId: string, sslEnabled?: boolean) {
    const nodeId = storeId + '-' + deviceId;
    console.log(`personalizing with server: ${serverName}, port: ${serverPort}, nodeid: ${nodeId}`);
    localStorage.setItem('serverName', serverName);
    localStorage.setItem('serverPort', serverPort);
    localStorage.setItem('nodeId', nodeId);
    if (sslEnabled) {
      localStorage.setItem('sslEnabled', '' + sslEnabled);
    } else {
      localStorage.setItem('sslEnabled', 'false');
    }
    this.refreshApp();
  }

  public dePersonalize() {
    this.unsubscribe();
    const theme = this.getTheme();
    localStorage.removeItem('serverName');
    localStorage.removeItem('serverPort');
    localStorage.removeItem('nodeId');
    localStorage.removeItem('theme');
    localStorage.removeItem('sslEnabled');
    this.setTheme(theme);
  }

  private getWebsocketUrl(): string {
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

  private buildTopicName(): string {
    return '/topic/app/' + this.appId + '/node/' + this.getNodeId();
  }

  public showScreen(screen: any) {
    this.screen = screen;
    if (screen && screen.theme) {
      this.setTheme(screen.theme);
    }
    this.screenSource.next(screen);
  }

  public showDialog(dialogObj: any) {
    if (!dialogObj) {
      this.dialog = null;
    } else if (dialogObj.template.dialog) {
      console.log(`SessionService.showDialog invoked. dialogObj: ${dialogObj}`);
      this.dialog = dialogObj;
      this.response = null;
    }
    this.dialogSource.next(this.dialog);
  }

  public getPersonalizationScreen(): any {
    // tslint:disable-next-line:max-line-length
    return { type: 'Personalization', sequenceNumber: Math.floor(Math.random() * 2000), name: 'Device Setup', refreshAlways: true, template: { type: 'Blank', dialog: false } };
  }

  public getTheme(): string {
    if (this.screen && this.screen.theme) {
      return this.screen.theme;
    } else {
      return localStorage.getItem('theme');
    }
  }

  public isSslEnabled(): boolean {
    return 'true' === localStorage.getItem('sslEnabled');
  }

  public setTheme(theme: string) {
    localStorage.setItem('theme', theme);
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

  public getAppId(): String {
    return this.appId;
  }

  public setServerPort(port: string) {
    localStorage.setItem('serverPort', port);
  }

  public getServerPort(): string {
    return localStorage.getItem('serverPort');
  }

  public getNodeId(): string {
    return localStorage.getItem('nodeId');
  }

  public setNodeId(id: string) {
    localStorage.setItem('nodeId', id);
  }

  public refreshApp() {
    window.location.href = 'index.html';
  }

  public isPersonalized(): boolean {
    if (this.getServerName() && this.getNodeId() && this.getServerPort()) {
      return true;
    } else {
      return false;
    }
  }

  public connected(): boolean {
    return this.stompService && this.stompService.connected();
  }

  public subscribe(appName: String) {
    if (this.subscribed) {
      return;
    }

    const url = this.getWebsocketUrl();
    console.log('creating new stomp service at: ' + url);
    this.stompService = new StompService({
      url: url,
      headers: {
        //    login: 'guest',
        //    passcode: 'guest'
      },
      heartbeat_in: 0, // Typical value 0 - disabled
      heartbeat_out: 20000, // Typical value 20000 - every 20 seconds
      reconnect_delay: 5000,
      debug: this.stompDebug
    });

    // Give preference to nodeId query parameter if it's present, then fallback to
    // local storage
    this.appId = appName;
    const currentTopic = this.buildTopicName();

    console.log('subscribing to server at: ' + currentTopic);

    this.messages = this.stompService.subscribe(currentTopic as string);

    // Subscribe a function to be run on_next message
    this.subscription = this.messages.subscribe(this.onNextMessage);

    this.state = this.stompService.state
      .map((state: number) => StompState[state]);

    this.subscribed = true;
  }

  public unsubscribe() {
    if (!this.subscribed) {
      return;
    }

    console.log('unsubscribing from stomp service ...');

    // This will internally unsubscribe from Stomp Broker
    // There are two subscriptions - one created explicitly, the other created in the template by use of 'async'
    this.subscription.unsubscribe();
    this.subscription = null;

    console.log('disconnecting from stomp service');
    this.stompService.disconnect();
    this.stompService = null;
    this.messages = null;

    this.subscribed = false;
  }

  public onDeviceResponse(deviceResponse: IDeviceResponse) {
    const sendResponseBackToServer: Function = () => {
      // tslint:disable-next-line:max-line-length
      console.log(`>>> Publish deviceResponse requestId: "${deviceResponse.requestId}" deviceId: ${deviceResponse.deviceId} type: ${deviceResponse.type}`);
      this.stompService.publish(`/app/device/app/${this.appId}/node/${this.getNodeId()}/device/${deviceResponse.deviceId}`,
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

  public async onAction(action: string | IMenuItem, payload?: any, confirm?: string, isValueChangedAction?: boolean) {
    if (action) {
      let actionString = '';
      // we need to figure out if we are a menuItem or just a string
      if (action.hasOwnProperty('action')) {
        const menuItem = <IMenuItem>(action);

        actionString = menuItem.action;
        // check to see if we are an IURLMenuItem
        if (menuItem.hasOwnProperty('url')) {
          const urlMenuItem = <IUrlMenuItem>menuItem;
          console.log(`About to open: ${urlMenuItem.url} in target mode: ${urlMenuItem.targetMode}, with options: ${urlMenuItem.options}`);
          window.open(urlMenuItem.url, urlMenuItem.targetMode, urlMenuItem.options);
          if (!actionString || 0 === actionString.length) {
            return;
          }
        }
      } else {
        actionString = <string>action;
      }

      console.log(`action is: ${actionString}`);

      if (confirm) {
        console.log('Confirming action');
        const dialogRef = this.dialogService.open(ConfirmationDialogComponent, { disableClose: true });
        dialogRef.componentInstance.title = confirm;
        const result = await dialogRef.afterClosed().toPromise();

        // if we didn't confirm return and don't send the action to the server
        if (!result) {
          console.log('Canceling action');
          return;
        }
      }

      let processAction = true;

      // First we will use the payload passed into this function then
      // Check if we have registered action payload
      // Otherwise we will send whatever is in this.response
      if (payload != null) {
        this.response = payload;
      } else if (this.actionPayloads.has(actionString)) {
        console.log(`Checking registered action payload for ${actionString}`);
        try {
          this.response = this.actionPayloads.get(actionString)();
        } catch (e) {
          console.log(`invalid action payload for ${actionString}: ` + e);
          processAction = false;
        }
      }

      if (processAction && !this.loading) {
        const sendToServer: Function = () => {
          console.log(`>>> Post action "${actionString}"`);
          this.publish(actionString);
        };

        // see if we have any intercepters registered
        // otherwise just send the action
        if (this.actionIntercepters.has(actionString)) {
          this.actionIntercepters.get(actionString).intercept(this.response, sendToServer);
        } else {
          sendToServer();
          if (!isValueChangedAction) {
            this.showDialog(null);
          }
          this.queueLoading();
        }
      } else {
        console.log(`Not sending action: ${actionString}.  processAction: ${processAction}, loading:${this.loading}`);
      }

    } else {
      console.log(`received an invalid action: ${action}`);
    }
  }

  public keepAlive() {
    console.log(`>>> KeepAlive`);
    this.publish('KeepAlive');
  }

  private publish (actionString: string) {
    this.stompService.publish('/app/action/app/' + this.appId + '/node/' + this.getNodeId(),
      JSON.stringify({ name: actionString, data: this.response }));
  }

  private queueLoading() {
    this.loading = true;
    setTimeout(() => this.showLoading(LoaderState.LOADING_TITLE), 1000);
  }

  private showLoading(title: string, message?: string) {
    if (this.loading) {
      this.loaderState.setVisible(true, title, message);
    }
  }

  public cancelLoading() {
    this.loading = false;
    this.loaderState.setVisible(false);
  }

  /** Consume a message from the stompService */
  public onNextMessage = (message: Message) => {
    const json = JSON.parse(message.body);
    if (json.clearDialog) {
      this.showDialog(null);
    } else if (json.type === 'Loading') { // This is just a temporary hack
      this.loading = true;
      this.showLoading(json.title, json.message);
      return;
    } else if (json.template && json.template.dialog) {
      this.showDialog(json);
    } else if (json.type === 'NoOp') {
      this.response = null;
      return; // As with DeviceRequest, return to avoid dismissing loading screen
    } else if (json.type === 'DeviceRequest') {
      this.onDeviceRequest.emit(json);
      // Return explicitly in the case that the prior
      // screen shown was a 'loading' screen, don't want to dismiss
      // that prematurely
      return;
    } else {
      this.response = null;
      this.showScreen(json);
      this.showDialog(null);
    }
    this.cancelLoading();
  }

  private getUrlNodeId() {
    const urlPath = this.location.path();
    let urlNodeId = '';
    if (urlPath) {
      const urlTree = this.router.parseUrl(urlPath);
      if (urlTree) {
        if (urlTree.queryParams['nodeId']) {
          urlNodeId = urlTree.queryParams['nodeId'];
          console.log('nodeId found on query parameters: [' + urlNodeId + ']');
        }
      }
    }
    return urlNodeId;
  }

  public registerActionPayload(actionName: string, actionValue: Function) {
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

  public getCurrencyDenomination(): string {
    return 'USD';
  }

  public getServerBaseURL(): string {
    if (! this.serverBaseUrl) {
      this.serverBaseUrl = `http://${this.getServerName()}${this.getServerPort() ? `:${this.getServerPort()}` : ''}`;
    }
    return this.serverBaseUrl;
  }

  public getApiServerBaseURL(): string {
    return `${this.getServerBaseURL()}/api`;
  }

  getLocale(): string {
    let returnLocale: string;
    if (this.screen) {
      returnLocale = this.screen.locale;
    }
    return returnLocale || DEFAULT_LOCALE;
  }

}


