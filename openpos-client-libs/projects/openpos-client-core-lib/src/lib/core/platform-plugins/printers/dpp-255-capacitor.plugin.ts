import {IPrinter} from "./printer.interface";
import {InfineaPlugin, IPlatformPlugin} from "../platform-plugin.interface";
import {Observable} from "rxjs";
import {Plugins as CapacitorPlugins} from "@capacitor/core/dist/esm/global";
import {Injectable} from "@angular/core";
import {ConfigurationService} from "../../services/configuration.service";
import {CapacitorService} from "../../services/capacitor.service";

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
    constructor(config: ConfigurationService, private capacitorService: CapacitorService) {
    }

    print(html: String) {
        CapacitorPlugins.Dpp255Capacitor.print({data: html});
    }

    initialize(): Observable<string> {
        return new Observable(observer => {
            CapacitorPlugins.Dpp255Capacitor.initialize();
            return CapacitorPlugins.Dpp255Capacitor.addListener('status', (e) => {
                observer.next("Infinea printer status: " + JSON.stringify(e));
            });
        });
    }

    name(): string {
        return "Dpp255Capacitor";
    }

    pluginPresent(): boolean {
        return this.capacitorService.isRunningInCapacitor() && this.capacitorService.isPluginAvailable('Dpp255Capacitor');
    }
}