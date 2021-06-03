import { Inject, Injectable, Optional } from '@angular/core';

import { of, from } from 'rxjs';
import { filter, first, map, mergeMap, single } from 'rxjs/operators';

import { ConfigurationService } from '../../services/configuration.service';
import { KIOSK_MODE_PLATFORM, KioskModePlatform, KioskModeHandle } from './kiosk-mode-platform';

@Injectable()
export class KioskModeController {
    private _selectedPlatform?: KioskModePlatform;

    constructor(
        private _config: ConfigurationService,
        @Inject(KIOSK_MODE_PLATFORM) @Optional() platformPlugins: KioskModePlatform[]
    ) {
        if (platformPlugins) {
            of(...platformPlugins).pipe(
                mergeMap(platform => platform.isAvailable().then(avail => ({platform, avail}))),
                filter(p => p.avail),
                first(),
                map(p => p.platform)
            ).subscribe(platform => {
                console.log(`using kiosk mode platform: '${platform.name()}'`);
                this._selectedPlatform = platform;
            });
        }
    }

    get isKioskModeAvailable(): boolean {
        return !!this._selectedPlatform;
    }

    async isInKioskMode(): Promise<boolean> {
        if (!this.isKioskModeAvailable) {
            return false;
        }

        return await this._selectedPlatform.isInKioskMode();
    }

    async enterKioskMode(): Promise<void> {
        this._verifyKioskModePlatformExists();

        if (await this.isInKioskMode()) {
            return;
        }

        console.debug(`attempting to enter kiosk mode on platform: '${this._selectedPlatform.name()}`);
        
        try {
            await this._selectedPlatform.enter();
        } catch (e) {
            console.error('failed to enter kiosk mode', e);
        }
    }

    async exitKioskMode(): Promise<void> {
        this._verifyKioskModePlatformExists();

        if (await this.isInKioskMode()) {
            console.debug(`attempting to exit kiosk mode on platform: '${this._selectedPlatform.name()}`);

            try {
                await this._selectedPlatform.exit();
            } catch (e) {
                console.error('failed to exit kiosk mode', e);
            }
        }
    }

    private _verifyKioskModePlatformExists() {
        if (!this._selectedPlatform) {
            throw new Error('no kiosk platform available');
        }
    }
}
