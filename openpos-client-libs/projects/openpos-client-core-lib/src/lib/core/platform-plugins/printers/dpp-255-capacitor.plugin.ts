import {IPrinter} from "./printer.interface";
import {InfineaPlugin, IPlatformPlugin} from "../platform-plugin.interface";
import {Observable, of} from "rxjs";
import {Capacitor} from "@capacitor/core";
import {Plugins as CapacitorPlugins} from "@capacitor/core/dist/esm/global";
import {Injectable} from "@angular/core";
import {ConfigChangedMessage} from "../../messages/config-changed-message";
import {ConfigurationService} from "../../services/configuration.service";

declare module '@capacitor/core' {
    interface PluginRegistry {
        Dpp255Capacitor: InfineaPlugin;
    }
}

@Injectable({
    providedIn: 'root'
})
export class Dpp255CapacitorPlugin implements IPrinter, IPlatformPlugin {
    id: string = 'DPP255CAP';
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

    print(html: String) {
        CapacitorPlugins.Dpp255Capacitor.print({data: html});
    }

    initialize(): Observable<string> {
        return of()
    }

    name(): string {
        return "Dpp255Capacitor";
    }

    pluginPresent(): boolean {
        return Capacitor.isNative && Capacitor.isPluginAvailable('Dpp255Capacitor');
    }
}