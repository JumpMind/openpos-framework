import { Inject, Injectable, Optional } from '@angular/core';

import { of, from } from 'rxjs';
import { filter, first, map, mergeMap, single } from 'rxjs/operators';

import { ConfigurationService } from '../../services/configuration.service';
import { KIOSK_MODE_PLATFORM, KioskModePlatform, KioskModeHandle } from './kiosk-mode-platform';

@Injectable()
export class KioskModeController {
    private _selectedPlatform?: KioskModePlatform;
    private _activeHandle?: KioskModeHandle;

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
                console.log(`using kiosk mode platform: '${platform.name()}`);
                this._selectedPlatform = platform;
            });
        }
    }

    get isKioskModeAvailable(): boolean {
        return !!this._selectedPlatform;
    }

    get isInKioskMode(): boolean {
        return !!this._activeHandle;
    }

    async enterKioskMode(): Promise<void> {
        this._verifyKioskModePlatformExists();

        if (this._activeHandle) {
            return;
        }

        console.debug(`attempting to enter kiosk mode on platform: '${this._selectedPlatform.name()}`);
        this._activeHandle = await this._selectedPlatform.enter();
    }

    async exitKioskMode(): Promise<void> {
        this._verifyKioskModePlatformExists();

        if (this._activeHandle) {
            console.debug(`attempting to exit kiosk mode on platform: '${this._selectedPlatform.name()}`);
            await this._activeHandle.exit();
            this._activeHandle = undefined;
        }
    }

    private _verifyKioskModePlatformExists() {
        if (!this._selectedPlatform) {
            throw new Error('no kiosk platform available');
        }
    }
}
