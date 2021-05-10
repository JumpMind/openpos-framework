import {InfineaPlugin, IPlatformPlugin} from "../platform-plugin.interface";
import {Observable, of} from "rxjs";
import {Plugins as CapacitorPlugins} from "@capacitor/core/dist/esm/global";
import {Injectable} from "@angular/core";
import {ConfigChangedMessage} from "../../messages/config-changed-message";
import {ConfigurationService} from "../../services/configuration.service";
import {CapacitorService} from "../../services/capacitor.service";

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
    constructor(config: ConfigurationService, private capacitorService: CapacitorService) {
        if(this.pluginPresent()) {
            config.getConfiguration('InfineaCapacitor').subscribe( (config: ConfigChangedMessage & any) => {
                if (config.licenseKey) {
                    CapacitorPlugins.InfineaSdk.initialize({
                        apiKey: config.licenseKey
                    });
                }
            });
        }
    }

    initialize(): Observable<string> {
        return of("Infinea SDK for Capacitor initialized")
    }

    name(): string {
        return "InfineaSdk";
    }

    pluginPresent(): boolean {
        return this.capacitorService.isRunningInCapacitor() && this.capacitorService.isPluginAvailable('InfineaSdk');
    }
}