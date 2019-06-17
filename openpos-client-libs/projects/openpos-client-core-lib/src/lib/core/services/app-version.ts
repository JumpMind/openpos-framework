import { OldPluginService } from './old-plugin.service';
import { Injectable } from '@angular/core';
import { IOldPlugin } from '../oldplugins/oldplugin.interface';

declare var version: any; // read in from core at assets/version.js

@Injectable({
    providedIn: 'root'
})
export class AppVersion {

    private _appVersion = null;
    private _buildVersion = null;

    public buildVersion(): string {
        if (!this._buildVersion) {
            this._buildVersion = typeof version === 'undefined' ? 'unknown' : version;
        }

        return this._buildVersion;
    }

    public appVersion(pluginService: OldPluginService): Promise<string> {
        let promiseReturn: Promise<string>;
        if (this._appVersion) {
            promiseReturn = new Promise<string>((resolve, reject) => { resolve(this._appVersion); });
        } else {
            promiseReturn = new Promise<string>(
            (resolve, reject) => {
                pluginService.getPlugin('openPOSCordovaLogPlugin').then(
                    (plugin: IOldPlugin) => {
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
