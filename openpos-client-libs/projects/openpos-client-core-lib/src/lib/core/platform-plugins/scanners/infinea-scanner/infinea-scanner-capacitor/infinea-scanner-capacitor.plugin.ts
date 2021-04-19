import {Injectable} from '@angular/core';
import {InfineaPlugin, IPlatformPlugin} from '../../../platform-plugin.interface';
import {IScanner} from '../../scanner.interface';
import {Observable, of} from 'rxjs';
import {IScanData} from '../../scan.interface';
import {Capacitor, Plugins as CapacitorPlugins} from "@capacitor/core";
import {ConfigurationService} from "../../../../services/configuration.service";
import {ConfigChangedMessage} from "../../../../messages/config-changed-message";
import {InfineaBarcodeUtils} from "../infinea-to-openpos-barcode-type";

declare module '@capacitor/core' {
    interface PluginRegistry {
        InfineaScannerCapacitor: InfineaPlugin;
    }
}

@Injectable({
    providedIn: 'root'
})
export class InfineaScannerCapacitorPlugin implements IPlatformPlugin, IScanner {
    constructor(config: ConfigurationService) {
        if(this.pluginPresent()) {
            config.getConfiguration('InfineaCapacitor').subscribe( (config: ConfigChangedMessage & any) => {
                if (config.licenseKey) {
                    CapacitorPlugins.InfineaScannerCapacitor.initialize({
                        apiKey: config.licenseKey
                    });
                }
            });
        }
    }

    name(): string {
        return 'InfineaScannerCapacitor';
    }

    pluginPresent(): boolean {
        return Capacitor.isNative && Capacitor.isPluginAvailable('InfineaScannerCapacitor');
    }

    initialize(): Observable<string> {
        return of()
    }

    startScanning(): Observable<IScanData> {
        return new Observable(observer => {
            const handle = CapacitorPlugins.InfineaScannerCapacitor.addListener('scan', (e) => {
                observer.next({
                    type: InfineaBarcodeUtils.convertToOpenposType(e.type),
                    data: e.barcode
                });
            });

            return () => {
                handle.remove();
            };
        });
    }

    stopScanning() {}

    triggerScan() {
    }
}
