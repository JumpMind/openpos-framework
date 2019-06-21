import { Logger } from './logger.service';
import { IMessageHandler } from './../interfaces/message-handler.interface';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { FileUploadService } from './file-upload.service';
import { OldPluginService } from './old-plugin.service';
import { SessionService } from './session.service';
import { CordovaService } from './cordova.service';
import { InAppBrowserPlugin } from '../oldplugins/in-app-browser.plugin';
import { BarcodeScannerPlugin } from '../oldplugins/barcode-scanner.plugin';
import { LogfileDownloadPlugin } from '../oldplugins/logfile-download.plugin';
import { Scan } from '../oldplugins/scan';
import { IDeviceRequest } from '../oldplugins/device-request.interface';
import { IDevicePlugin } from '../oldplugins/device-plugin.interface';
import { DEVICE_RESPONSE_TYPE, DEVICE_ERROR_RESPONSE_TYPE, DEVICE_DNE_RESPONSE_TYPE } from '../oldplugins/device-response.interface';

@Injectable({
    providedIn: 'root',
})
export class DeviceService implements IMessageHandler<any> {

    public onDeviceReady: Subject<string> = new BehaviorSubject<string>(null);
    public onAppEnteringBackground: Subject<boolean> = new BehaviorSubject<boolean>(null);
    public onAppEnteringForeground: Subject<boolean> = new BehaviorSubject<boolean>(null);
    public onAppEnteredForeground: Subject<boolean> = new BehaviorSubject<boolean>(null);

    private screen: any;

    private cameraScanInProgress = false;

    constructor(private log: Logger, protected session: SessionService,
        private cordovaService: CordovaService,
        public pluginService: OldPluginService,
        private fileUploadService: FileUploadService) {

        // On iOS need to enter into loading state when the app is backgrounded, otherwise
        // user can execute actions as app is coming back to foreground.

        this.onAppEnteringBackground.subscribe(async (backgrounded) => {
            if (backgrounded) {
                const allowBackgroundHandling = !this.isCameraScanInProgress() && ! await this.isInAppBrowserActive();
                if (allowBackgroundHandling) {
                    this.handleBackgrounding();
                } else {
                    this.log.info(`Skipping background handling`);
                }
            }
        });

        this.onAppEnteredForeground.subscribe(foregrounded => {
            const allowForegroundHandling = this.session.inBackground;
            if (foregrounded) {
                if (allowForegroundHandling) {
                    this.handleForegrounding();
                } else {
                    this.log.info(`Skipping foreground handling`);
                }
            }
        });

        this.cordovaService.onDeviceReady.subscribe(m => {
            if (m === 'deviceready') {
                this.log.info('cordova devices are ready for the device service');
                this.initializeInAppBrowserPlugin();
                this.initializeBarcodeScannerPlugin();
                this.initializeLogfileDownloadPlugin();
                this.onDeviceReady.next(`Application is initialized on platform '${this.cordovaService.cordova.platform}'`);
            }
        });
        // For iOS in particular listen for the 'resign' event which is sent as we are either transitioning into the background
        // or resiging status as the active app.
        document.addEventListener('resign',
            () => {
                this.log.info(`OpenPOS received app 'resign' notification`);
                this.onAppEnteringBackground.next(true);
            },
            false
        );
        document.addEventListener('resume',
            () => {
                this.log.info(`OpenPOS received app 'resume' notification`);
                this.onAppEnteringForeground.next(true);
            },
            false
        );
        document.addEventListener('active',
            () => {
                this.log.info(`OpenPOS received app 'active' notification`);
                this.onAppEnteredForeground.next(true);
            },
            false
        );

        this.session.registerMessageHandler(this, 'DeviceRequest', 'Screen');

    }

    public async isInAppBrowserActive() {
        try {
            const inAppPlugin = <InAppBrowserPlugin>await this.pluginService.getPlugin('InAppBrowser');
            return inAppPlugin.isActive();
        } catch (error) {
            this.log.info(`InAppBrowser plugin not available. Reason: ${error}`);
            return false;
        }
    }

    public isCameraScanInProgress() {
        this.log.info(`isCameraScanInProgress? ${this.cameraScanInProgress}`);
        return this.cameraScanInProgress;
    }

    handle(message: any) {
        if (message && message.type === 'Screen' &&  message.template && !message.template.dialog) {
            this.screen = message;
        } else if (message && message.type === 'DeviceRequest') {
            this.onDeviceRequest(message);
        }
    }

    protected initializeBarcodeScannerPlugin(): void {
        // Add barcdode scanner plugin as a device plugin so it can also be invoked
        // from the server-side in addition to from within the DeviceService
        const barcodeScannerPlugin = new BarcodeScannerPlugin();
        this.pluginService.addPlugin(barcodeScannerPlugin.pluginName, barcodeScannerPlugin);
    }

    protected initializeInAppBrowserPlugin(): void {
        const inAppBrowserPlugin = new InAppBrowserPlugin();
        this.pluginService.addPlugin(inAppBrowserPlugin.pluginId, inAppBrowserPlugin);
        this.log.info('InAppBrowserPlugin initialized.');
    }

    protected initializeLogfileDownloadPlugin(): void {
        const logfileDownloadPlugin = new LogfileDownloadPlugin(this.fileUploadService);
        this.pluginService.addPlugin(logfileDownloadPlugin.pluginId, logfileDownloadPlugin);
        this.log.info('LogfileDownloadPlugin initialized.');
    }

    public scan(source?: string) {
        // TODO: should be able to modify this method to pass in the screen instead of 
        // subscribing to all screen updates.  The only place that invokes this method
        // is the scan-something.component.
        if (this.screen.template && this.screen.template.scan &&
            this.screen.template.scan.scanType === 'CAMERA_CORDOVA') {
            this.log.info(`request to scan was made for: ${this.screen.template.scan.scanType}`);
            this.cordovaCameraScan(source);
        } else {
            this.log.info(`FAILED to invoke scan. Is there a screen.template.scan.scanType?`);
        }
    }

    public cordovaCameraScan(source?: string) {
        if (this.cordovaService.isRunningInCordova()) {
            this.pluginService.getDevicePlugin('barcodeScannerPlugin').then(plugin => {
                this.cameraScanInProgress = true;
                plugin.processRequest(
                    { requestId: 'scan', deviceId: 'barcode-scanner', type: null, subType: null, payload: null },
                    (response) => {
                        if (response && !response.cancelled) {
                            if (response instanceof Scan && source && !response.source) {
                                response.source = source;
                            }
                            this.session.onAction('Scan', response);
                        }
                        this.cameraScanInProgress = false;
                    },
                    (error) => {
                        this.cameraScanInProgress = false;
                        this.log.error('Scanning failed: ' + error);
                    }
                );
            }).catch(error => {
                this.cameraScanInProgress = false;
                this.log.error(`barcodeScannerPlugin error: ${error}`);
            });
        }
    }

    public onDeviceRequest = (request: IDeviceRequest) => {
        this.log.info(`request received for device: ${request.deviceId}`);
        // targetted plugin is assumed to be a cordova plugin

        const pluginLookupKey = request.pluginId ? request.pluginId : request.deviceId;
        const targetPluginPromise: Promise<IDevicePlugin> = this.pluginService.getDevicePlugin(pluginLookupKey);

        targetPluginPromise.then((targetPlugin: IDevicePlugin) => {
            this.log.info(`targetPlugin = pluginId: ${targetPlugin.pluginId}, pluginName: ${targetPlugin.pluginName}`);
            this.log.info(`Sending request '${request.subType}:${request.requestId}' to device/plugin '${pluginLookupKey}'...`);
            targetPlugin.processRequest(
                request,
                (response) => {
                    this.session.onDeviceResponse({
                        requestId: request.requestId,
                        deviceId: request.deviceId,
                        type: DEVICE_RESPONSE_TYPE,
                        payload: response
                    }
                    );
                },
                (error) => {
                    this.session.onDeviceResponse({
                        requestId: request.requestId,
                        deviceId: request.deviceId,
                        type: DEVICE_ERROR_RESPONSE_TYPE,
                        payload: error
                    }
                    );
                }
            );
        }
        ).catch((error) => {
            const msg = 'No handling yet (or plugin may not be initialized) for ' +
                `device/plugin with key: ${pluginLookupKey}. request '${request.subType}:${request.requestId}' will be ignored. Error: ${error}`;

            this.session.onDeviceResponse({
                requestId: request.requestId,
                deviceId: request.deviceId,
                type: DEVICE_DNE_RESPONSE_TYPE,
                payload: msg
            }
            );
            this.log.info(msg);
        }
        );
    }

    public isRunningInCordova(): boolean {
        return this.cordovaService.isRunningInCordova();
    }

    public isRunningInBrowser(): boolean {
        return this.session.isRunningInBrowser();
    }


    private handleBackgrounding() {
        /* 10/12/18 -- Commented out showing of splashscreen due to improved show/cancel loading
            that we added.  Will remove once fully vetted in QA.
        // Show splash screen when running on iOS
        // Ascena has a Startup task that automatically dismisses the splash screen.
        // If other retailers are using iOS will need to implement the same, move this code
        // to Ascena, or find the right place in the core to dismiss the splash screen

        if ((<any>(window.navigator)).splashscreen) {
            this.log.info('Showing splashscreen');
            (<any>(window.navigator)).splashscreen.show();
        }
        */
        this.session.cancelLoading();
        // Input will get unblocked once re-subscribed to server and current screen is shown
        this.log.info('Entering into background');
        this.session.inBackground = true;
    }

    private handleForegrounding() {
        // check for any changes while were are inactive
        // We'll reset the inBackground flag after we receive the response
        this.session.refreshScreen();
        this.log.info('Start coming into foreground. Screen refresh requested.');
        /* 10/12/18 -- Commented out hiding of splashscreen due to improved show/cancel loading
            that we added.  Will remove once fully vetted in QA.
        if ((<any>(window.navigator)).splashscreen) {
            this.log.debug('Hiding splashscreen');
            (<any>(window.navigator)).splashscreen.hide();
        }
        */
    }

}
