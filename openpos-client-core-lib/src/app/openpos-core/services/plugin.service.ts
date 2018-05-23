import { CordovaDevicePlugin } from './../common/cordova-device-plugin';
import { CordovaPlugin } from './../common/cordova-plugin';
import { IDevicePlugin } from './../common/idevice-plugin';
import { IPlugin } from './../common/iplugin';
import { Injectable } from '@angular/core';

declare var cordova: any;

@Injectable()
export class PluginService {

    private plugins = new Map<string, PluginMapEntry>();

    constructor() {
        document.addEventListener('deviceready', () => {
            console.log('cordova devices are ready for the plugin service');
            // cordova file plugin doesn't put itself in cordova.plugins, so add it there if present.
            // Makes it possible for us to access plugins dynamically by name.
            // There apparently is not a consistent way to access references to
            // cordova plugins.
            if (cordova.file) {
                if (! cordova.plugins || ! cordova.plugins['file']) {
                    cordova.plugins['file'] = cordova.file;
                    console.log('PluginService added cordova-plugin-file to cordova.plugins');
                }
            }
          },
          false);
    }

    public pluginExists(pluginId: string): boolean {
        return this.plugins.has(pluginId);
    }

    public addPlugin(pluginId: string, plugin: IPlugin) {
        this.plugins[pluginId] = {plugin: plugin, initialized: false};
        console.log(`plugin '${pluginId}' added to the PluginService`);
    }

    public configurePlugin(pluginId: string, pluginConfig: any): Promise<boolean> {
        console.log(`Configuring plugin '${pluginId}'...`);
        return new Promise<boolean>( (resolve, reject) => {
            this.getPlugin(pluginId, false).then(
                plugin => {
                    console.log(`Got plugin '${pluginId}'...`);
                    if (typeof plugin.configure !== 'undefined') {
                        console.log(`Invoking plugin.configure for '${pluginId}'...`);
                        resolve(plugin.configure(pluginConfig));
                    } else if (plugin.impl && typeof plugin.impl.configure === 'function') {
                        console.log(`Invoking plugin.impl.configure for '${pluginId}'...`);
                        resolve(plugin.impl.configure(pluginConfig));
                    } else if (plugin.hasOwnProperty('config')) {
                        console.log(`Setting plugin.config for '${pluginId}'...`);
                        plugin.config = pluginConfig;
                        resolve(true);
                    } else if (plugin.impl && plugin.impl.hasOwnProperty('config')) {
                        console.log(`Setting plugin.impl.config for '${pluginId}'...`);
                        plugin.impl.config = pluginConfig;
                        resolve(true);
                    } else {
                        console.log(`No method of configuration is available for plugin '${pluginId}'`);
                        resolve(false);
                    }
                }
            ).catch(
                (error) => {
                    console.log(error);
                    reject(error);
                }
            );
          }
        );
    }

    public getPlugin(pluginId: string, doInitWhenNeeded: boolean = true): Promise<IPlugin> {
        return new Promise( (resolve, reject) => {
            console.log(`Getting plugin '${pluginId}'...`);
            let pluginEntry: PluginMapEntry = this.plugins[pluginId];
            let initRequired = false;
            let targetPlugin: IPlugin;
            if (pluginEntry) {
                initRequired = ! pluginEntry.initialized;
                targetPlugin = pluginEntry.plugin;
                console.log(`Plugin '${pluginId}' found. initRequired? ${initRequired}`);
            } else {
                console.log(`Plugin '${pluginId}' is being fetched for the first time.`);
                if (this.isCordovaPlugin(pluginId)) {
                    // let cdvPlugin = null;
                    if (cordova.plugins && cordova.plugins[pluginId]
                        && typeof cordova.plugins[pluginId].processRequest !== 'undefined') {
                        targetPlugin = new CordovaDevicePlugin(pluginId);
                    } else {
                        targetPlugin = new CordovaPlugin(pluginId);
                    }

                    pluginEntry = {plugin: targetPlugin, initialized: false};
                    this.plugins[pluginId] = pluginEntry;
                    console.log(`Added plugin '${pluginId}' to map.`);
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
                console.log(`Initializing plugin '${pluginId}'...`);
                this.pluginInit(targetPlugin).then(
                    (inittedPlugin) => {
                        pluginEntry.initialized = true;
                        // this.plugins[pluginId] = inittedPlugin;
                        // plugin = inittedPlugin;
                        console.log(`plugin '${pluginId}' initialized`);
                        resolve(inittedPlugin);
                    }
                ).catch(
                    (error) => {
                        if (error) {
                            reject(error);
                            console.log(error);
                        } else {
                            const err = `plugin '${pluginId}' failed to initialize`;
                            console.log(err);
                            reject(err);
                        }
                    }
                );
            } else {
                console.log(`Init of plugin '${pluginId}' not required.`);
                resolve(targetPlugin);
            }
        });
    }

    public getDevicePlugin(pluginId: string): Promise<IDevicePlugin> {
        return new Promise<IDevicePlugin>( (resolve, reject) => {
                const pluginPromise: Promise<IPlugin> = this.getPlugin(pluginId);
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

    private pluginInit(plugin: IPlugin): Promise<IPlugin> {
        const returnPromise: Promise<IPlugin> = new Promise(
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
        return typeof cordova !== 'undefined'
            && typeof cordova.plugins !== 'undefined'
            && (cordova.plugins[pluginId]);
    }

}

export interface PluginMapEntry {
    plugin: IPlugin;
    initialized: boolean;
}
