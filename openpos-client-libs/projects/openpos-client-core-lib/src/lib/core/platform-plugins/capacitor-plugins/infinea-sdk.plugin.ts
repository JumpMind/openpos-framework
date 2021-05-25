import {InfineaPlugin, IPlatformPlugin} from "../platform-plugin.interface";
import {from, Observable, of, throwError } from "rxjs";
import {Plugins as CapacitorPlugins} from "@capacitor/core/dist/esm/global";
import {Injectable} from "@angular/core";
import {ConfigChangedMessage} from "../../messages/config-changed-message";
import {ConfigurationService} from "../../services/configuration.service";
import {CapacitorService} from "../../services/capacitor.service";
import {catchError, flatMap} from "rxjs/operators";

declare module '@capacitor/core' {
    interface PluginRegistry {
        InfineaSdk: InfineaPlugin;
    }
}

@Injectable({
    providedIn: 'root'
})
export class InfineaSdkPlugin implements IPlatformPlugin {
    id: string = 'INFINEA_SDK';
    initializeObservable: Observable<any>;

    constructor(private config: ConfigurationService, private capacitorService: CapacitorService) {
        if (this.pluginPresent()) {
            config.getConfiguration('InfineaCapacitor').subscribe((config: ConfigChangedMessage & any) => {
                if (config.licenseKey) {
                    this.initializeObservable = from(CapacitorPlugins.InfineaSdk.initialize({
                        apiKey: config.licenseKey
                    }));
                }
            });
        }
    }

    initialize(): Observable<string> {
        return this.initializeObservable.pipe(
            flatMap(() => of("Infinea SDK initialized")),
            catchError(e => {
                return throwError(e);
            })
        );
    }

    name(): string {
        return "InfineaSdk";
    }

    pluginPresent(): boolean {
        return this.capacitorService.isRunningInCapacitor() && this.capacitorService.isPluginAvailable('InfineaSdk');
    }
}