import { AppInjector } from '../app-injector';
import { Logger } from '../services/logger.service';
import { IOldPlugin } from './oldplugin.interface';

declare var cordova: any;

export class CordovaPlugin implements IOldPlugin {
    pluginId: string;
    pluginName?: string;
    impl: any;
    protected log: Logger;

    constructor(pluginId: string) {
        this.pluginId = pluginId;
        this.log = AppInjector.Instance.get(Logger);
        if (typeof cordova !== 'undefined') {
            if (cordova.plugins && cordova.plugins[pluginId]) {
                this.impl = cordova.plugins[pluginId];
                this.pluginName = this.impl.pluginName;
            } else if (cordova[pluginId]) {
                this.impl = cordova[pluginId];
                this.pluginName = pluginId;
            } else {
                this.log.info(`Unable to locate cordova '${pluginId}' plugin.`);
            }
        }
    }

    init(successCallback: () => void, errorCallback: (error?: string) => void): void {
        if (typeof this.impl.init !== 'undefined') {
            this.impl.init(successCallback, errorCallback);
        } else {
            successCallback();
        }
    }
}
