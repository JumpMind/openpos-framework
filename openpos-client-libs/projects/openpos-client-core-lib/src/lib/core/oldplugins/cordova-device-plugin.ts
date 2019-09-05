import { AppInjector } from './../app-injector';
import { Logger } from './../services/logger.service';
import { IDeviceRequest } from './device-request.interface';
import { IDevicePlugin } from './device-plugin.interface';

declare var cordova: any;

export class CordovaDevicePlugin implements IDevicePlugin {
    pluginId: string;
    pluginName?: string;
    impl: any;
    protected log: Logger;

    constructor(pluginId: string) {
        this.log = AppInjector.Instance.get(Logger);
        this.pluginId = pluginId;
        if (typeof cordova !== 'undefined') {
            if ( typeof cordova.plugins !== 'undefined' && cordova.plugins[pluginId] ) {
                this.impl = cordova.plugins[pluginId];
                this.pluginName = this.impl.pluginName;
            } else {
                this.log.warn(`No plugin ${pluginId} found`);
            }
        }
    }

    init(successCallback: () => void, errorCallback: (error?: string) => void): void {
        if (typeof this.impl.init === 'function') {
            this.impl.init(successCallback, errorCallback);
        } else {
            this.log.info(`plugin '${this.pluginId} does not have init() method, nothing to do`);
            successCallback();
        }
    }

    processRequest(request: IDeviceRequest, successCallback: (response: any) => any, errorCallback: (error: string) => any) {
        this.impl.processRequest(() => request.payload, successCallback, errorCallback);
    }
}
