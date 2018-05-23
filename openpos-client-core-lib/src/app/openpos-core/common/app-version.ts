import { IPlugin } from './iplugin';
import { PluginService } from '../services';
import { Injectable } from '@angular/core';

declare var version: any; // read in from core at assets/version.js

@Injectable()
export class AppVersion {

    private _appVersion = null;
    private _buildVersion = null;

    constructor() {
    }

    public buildVersion(): string {
        if (!this._buildVersion) {
            this._buildVersion = typeof version === 'undefined' ? 'unknown' : version;
        }

        return this._buildVersion;
    }

    public appVersion(pluginService: PluginService): Promise<string> {
        let promiseReturn: Promise<string>;
        if (this._appVersion) {
            promiseReturn = new Promise<string>((resolve, reject) => { resolve(this._appVersion); });
        } else {
            promiseReturn = new Promise<string>(
            (resolve, reject) => {
                pluginService.getPlugin('openPOSCordovaLogPlugin').then(
                    (plugin: IPlugin) => {
                        if (plugin.impl) {
                            plugin.impl.getAppVersion(
                              (appVersion) => {
                                  this._appVersion = appVersion;
                                  resolve(appVersion);
                              },
                              (error) => {
                                reject(`Failed to get client app version.  Reason: ${error}`);
                              }
                            );
                        } else {
                            this._appVersion = 'n/a';
                            reject(`Failed to get client app version.  Reason: logging plugin not loaded`);
                        }
                    }
                ).catch(error => {
                    this._appVersion = 'n/a';
                    reject(`Couldn't get client app version, not available`);
                });
            });
        }
        return promiseReturn;
    }

}
