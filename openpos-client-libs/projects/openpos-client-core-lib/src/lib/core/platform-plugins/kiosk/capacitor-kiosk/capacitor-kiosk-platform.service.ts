import { Injectable } from '@angular/core';
import { Capacitor, Plugins } from '@capacitor/core';
import { KioskModeHandle, KioskModePlatform } from '../kiosk-mode-platform';

const { KioskMode } = Plugins;

@Injectable()
export class CapacitorKioskModePlatform implements KioskModePlatform {
    name(): string { return 'capacitor'; }

    isAvailable(): Promise<boolean> {
        return Promise.resolve(Capacitor.isPluginAvailable("KioskMode"));
    }

    async enter(): Promise<KioskModeHandle> {
        if (!await this.isAvailable()) {
            throw 'capacitor plugin is not available';
        }

        if (!await KioskMode.enterKioskMode()) {
            throw Error('failed to enter kiosk mode');
        }

        return new KioskModeHandle(async () => {
            KioskMode.exitKioskMode();
        });
    }
}
