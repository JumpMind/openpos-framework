import { Logger } from './logger.service';
import { Injectable } from '@angular/core';
import { CordovaService } from './cordova.service';
import { IOldPlugin } from '../oldplugins/oldplugin.interface';
import { CordovaDevicePlugin } from '../oldplugins/cordova-device-plugin';
import { CordovaPlugin } from '../oldplugins/cordova-plugin';
import { IDevicePlugin } from '../oldplugins/device-plugin.interface';


@Injectable({
    providedIn: 'root',
  })
export class OldPluginService {

    private plugins = new Map<string, PluginMapEntry>();
    // private onDeviceReady: BehaviorSubject<string> = new BehaviorSubject<string>(null);
    // private onDeviceReadySubscription: Subscription;

    constructor(private log: Logger, private cordovaService: CordovaService) {
        this.cordovaService.onDeviceReady.subscribe(m => {
            if (m === 'deviceready') {
                this.log.info('cordova devices are ready for the plugin service');
                // cordova file plugin doesn't put itself in cordova.plugins, so add it there if present.
                // Makes it possible for us to access plugins dynamically by name.
                // There apparently is not a consistent way to access references to
                // cordova plugins.
                if (this.cordovaService.cordova.file) {
                    if (! this.cordovaService.plugins || ! this.cordovaService.plugins['file']) {
                        this.cordovaService.plugins['file'] = this.cordovaService.cordova.file;
                        this.log.info('PluginService added cordova-plugin-file to cordova.plugins');
                    }
                } else {
                    this.log.info(`Failed to load the Cordova 'file' plugin. Log file uploads will not work unless this is resolved.` +
                        ` Is the Cordova 'file' plugin included in the project?`);
                }
            }
        });
    }

    public pluginExists(pluginId: string): boolean {
        return this.plugins.has(pluginId);
    }

    public addPlugin(pluginId: string, plugin: IOldPlugin) {
        this.plugins.set(pluginId, {plugin, initialized: false});
        this.log.info(`plugin '${pluginId}' added to the PluginService`);
    }

    public configurePlugin(pluginId: string, pluginConfig: any): Promise<boolean> {
        this.log.info(`Configuring plugin '${pluginId}'...`);
        return new Promise<boolean>( (resolve, reject) => {
            this.getPlugin(pluginId, false).then(
                plugin => {
                    this.log.info(`Got plugin '${pluginId}'...`);
                    if (typeof plugin.configure !== 'undefined') {
                        this.log.info(`Invoking plugin.configure for '${pluginId}'...`);
                        resolve(plugin.configure(pluginConfig));
                        this.log.info(`'${pluginId}' configured.`);
                    } else if (plugin.impl && typeof plugin.impl.configure === 'function') {
                        this.log.info(`Invoking plugin.impl.configure for '${pluginId}'...`);
                        resolve(plugin.impl.configure(pluginConfig));
                        this.log.info(`'${pluginId}' configured.`);
                    } else if (plugin.hasOwnProperty('config')) {
                        this.log.info(`Setting plugin.config for '${pluginId}'...`);
                        plugin.config = pluginConfig;
                        resolve(true);
                        this.log.info(`'${pluginId}' configured.`);
                    } else if (plugin.impl && plugin.impl.hasOwnProperty('config')) {
                        this.log.info(`Setting plugin.impl.config for '${pluginId}'...`);
                        plugin.impl.config = pluginConfig;
                        resolve(true);
                        this.log.info(`'${pluginId}' configured.`);
                    } else {
                        this.log.info(`No method of configuration is available for plugin '${pluginId}'`);
                        resolve(false);
                    }
                }
            ).catch(
                (error) => {
                    this.log.info(error);
                    reject(error);
                }
            );
          }
        );
    }

    public getPluginWithOptions(pluginId: string, doInitWhenNeeded: boolean = true,
      options?: {waitForCordovaInit?: boolean}): Promise<IOldPlugin> {
        // waitForCordovaInit addresses a race condition where a cordova dependent plugin
        // could be attempted to be fetched before it has been added to the plugin service.
        // I believe this was happening the barcodescanner plugin. May need to revisit how
        // this is done in the future.
        if (options && options.waitForCordovaInit) {
            return new Promise((resolve, reject) => {
                if (this.cordovaService.isRunningInCordova()) {
                    // After we added the corodova-monkey-patch-fix, cordova no longer published
                    // 'deviceready' event upon subscription after cordova had initially
                    // posted the deviceready event. I am working around that problem by using
                    // a BehaviorSubject internally to this class to mimic prior behavior
                    // provided by cordova.
                    this.cordovaService.onDeviceReady.subscribe(ready => {
                        if (ready) {
                            this.getPlugin(pluginId, doInitWhenNeeded).then( plugin => {
                                resolve(plugin);
                            }).catch( error => {
                                reject(error);
                            });
                        }
                    });
                } else {
                    reject(`Cordova not installed, plugin '${pluginId}' won't be fetched`);
                }
            });
        } else {
            return this.getPlugin(pluginId, doInitWhenNeeded);
        }
    }

    public getPlugin(pluginId: string, doInitWhenNeeded: boolean = true): Promise<IOldPlugin> {
        return new Promise( (resolve, reject) => {
            this.log.debug(`Getting plugin '${pluginId}'...`);
            let pluginEntry: PluginMapEntry = this.plugins.get(pluginId);
            let initRequired = false;
            let targetPlugin: IOldPlugin;
            if (pluginEntry) {
                initRequired = ! pluginEntry.initialized;
                targetPlugin = pluginEntry.plugin;
                this.log.debug(`Plugin '${pluginId}' found. initRequired? ${initRequired}`);
            } else {
                this.log.info(`Plugin '${pluginId}' is being fetched for the first time.`);
                if (this.isCordovaPlugin(pluginId)) {
                    if (typeof this.cordovaService.plugins[pluginId].processRequest !== 'undefined') {
                        targetPlugin = new CordovaDevicePlugin(pluginId);
                    } else {
                        targetPlugin = new CordovaPlugin(pluginId);
                    }

                    pluginEntry = {plugin: targetPlugin, initialized: false};
                    this.plugins.set(pluginId, pluginEntry);
                    this.log.info(`Added plugin '${pluginId}' to map.`);
                    if (doInitWhenNeeded) {
                        initRequired = true;
                    }
                } else {
                    const msg = `plugin '${pluginId}' not found`;
                    reject(msg);
                    return;
                    // handle future plugins here
                }
            }

            if (doInitWhenNeeded && initRequired) {
                this.log.info(`Initializing plugin '${pluginId}'...`);
                this.pluginInit(targetPlugin).then(
                    (inittedPlugin) => {
                        pluginEntry.initialized = true;
                        // this.plugins[pluginId] = inittedPlugin;
                        // plugin = inittedPlugin;
                        this.log.info(`plugin '${pluginId}' initialized`);
                        resolve(inittedPlugin);
                    }
                ).catch(
                    (error) => {
                        if (error) {
                            reject(error);
                            this.log.info(error);
                        } else {
                            const err = `plugin '${pluginId}' failed to initialize`;
                            this.log.info(err);
                            reject(err);
                        }
                    }
                );
            } else {
                this.log.debug(`Init of plugin '${pluginId}' not required.`);
                resolve(targetPlugin);
            }
        });
    }

    public getDevicePlugin(pluginId: string, doInitWhenNeeded: boolean = true): Promise<IDevicePlugin> {
        return new Promise<IDevicePlugin>( (resolve, reject) => {
                const pluginPromise: Promise<IOldPlugin> = this.getPlugin(pluginId, doInitWhenNeeded);
                pluginPromise.then(thePlugin => {
                    if (thePlugin && (<IDevicePlugin>thePlugin).processRequest) {
                        resolve(<IDevicePlugin> thePlugin);
                    } else {
                        resolve(null);
                    }
                }).catch(
                    (error) => { reject(error); }
                );
            }
        );
    }

    private pluginInit(plugin: IOldPlugin): Promise<IOldPlugin> {
        const returnPromise: Promise<IOldPlugin> = new Promise(
          (resolve, reject) => {
            plugin.init(
                () => {
                    resolve(plugin);
                },
                (error) => {
                    reject(error);
                }
            );
          }
        );

        return returnPromise;
    }
    /*
     * Assumes plugin is in cordova.plugins data structure
     * See plugin.xml for the plugin name at /plugin/platform/js-module/clobbers[@target]
     * @target should be set to location where the plugin can be accessed,
     * which needs to be cordova.plugins.xyz (where xyz is the pluginId) in order
     * for the plugin to be found.
     */
    public isCordovaPlugin(pluginId: string): boolean {
        return this.cordovaService.isRunningInCordova() &&
            this.cordovaService.plugins && this.cordovaService.plugins[pluginId];
    }

}

export interface PluginMapEntry {
    plugin: IOldPlugin;
    initialized: boolean;
}
