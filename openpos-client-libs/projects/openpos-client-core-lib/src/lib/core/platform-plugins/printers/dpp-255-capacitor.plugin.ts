import {IPrinter} from "./printer.interface";
import {InfineaPlugin, IPlatformPlugin} from "../platform-plugin.interface";
import {from, Observable, of, throwError} from "rxjs";
import {Plugins as CapacitorPlugins} from "@capacitor/core/dist/esm/global";
import {Injectable} from "@angular/core";
import {ConfigurationService} from "../../services/configuration.service";
import {CapacitorService} from "../../services/capacitor.service";
import {catchError, flatMap} from "rxjs/operators";

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
        return from(CapacitorPlugins.Dpp255Capacitor.initialize()).pipe(
            flatMap(() => {
                CapacitorPlugins.Dpp255Capacitor.addListener('status', (e) => {
                    console.log("Infinea printer status: " + JSON.stringify(e));
                });
                return of("DPP Printer initialized");
            }),
            catchError(e => {
                return throwError(e);
            })
        )
    }

    name(): string {
        return "Dpp255Capacitor";
    }

    pluginPresent(): boolean {
        return this.capacitorService.isRunningInCapacitor() && this.capacitorService.isPluginAvailable('Dpp255Capacitor');
    }
}