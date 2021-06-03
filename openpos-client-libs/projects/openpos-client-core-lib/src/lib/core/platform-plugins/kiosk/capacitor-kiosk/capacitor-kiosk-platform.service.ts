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

    async isInKioskMode(): Promise<boolean> {
        await this._verifyCapacitorPluginIsLoaded();

        return (await KioskMode.isInKioskMode()).enabled;
    }

    async enter(): Promise<void> {
        await this._verifyCapacitorPluginIsLoaded();

        if (!(await KioskMode.enter()).success) {
            throw Error('failed to enter kiosk mode');
        }
    }

    async exit(): Promise<void> {
        await this._verifyCapacitorPluginIsLoaded();

        if (!(await KioskMode.exit()).success) {
            throw Error('failed to exit kiosk mode');
        }
    }

    private async _verifyCapacitorPluginIsLoaded(): Promise<void> {
        if (!await this.isAvailable()) {
            throw 'capacitor plugin is not available';
        }
    }
}
