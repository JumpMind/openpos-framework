import { Injectable } from '@angular/core';
import {Capacitor, Plugins as CapacitorPlugins} from "@capacitor/core";

declare var cordova: any;

@Injectable({
    providedIn: 'root',
})
export class CapacitorService {

    public isRunningInCapacitor(): boolean {
        return Capacitor.isNative;
    }

    public isPluginAvailable(plugin: string): boolean {
        return Capacitor.isPluginAvailable(plugin);
    }
}


