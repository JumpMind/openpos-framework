import { Observable } from 'rxjs';
import {PluginListenerHandle} from "@capacitor/core";

export interface IPlatformPlugin {

    /**
     * Unique name to identify this plugin
     */
    name(): string;

    /**
     * If the plug in is not present it will be skipped by the start up task
     */
    pluginPresent(): boolean;

    /**
     * Creates an Observable task that will initilize the plugin and stream back
     * status messages and complete when done.
     */
    initialize(): Observable<string>;
}

export interface InfineaPlugin {
    addListener(event: 'scan', callback: (e) => void): PluginListenerHandle;
}
