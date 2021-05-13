import {Injectable} from '@angular/core';
import {Capacitor, DeviceInfo, Plugins} from "@capacitor/core";
import {from, Observable} from "rxjs";
import {map} from "rxjs/operators";

const {Device} = Plugins;

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

    public getDeviceName(): Observable<string> {
        return from(Device.getInfo()).pipe(
            map((info: DeviceInfo) => info.name)
        );
    }
}


