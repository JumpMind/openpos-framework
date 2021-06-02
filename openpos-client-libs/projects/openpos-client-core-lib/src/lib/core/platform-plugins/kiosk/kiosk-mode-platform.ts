import { InjectionToken } from '@angular/core';

export const KIOSK_MODE_PLATFORM = new InjectionToken<KioskModePlatform[]>('KIOSK_MODE_PLATFORM');

export interface KioskModePlatform {
    name(): string;
    isAvailable(): Promise<boolean>;
    enter(): Promise<KioskModeHandle>;
}

export class KioskModeHandle {
    private _hasExited = false;

    constructor(
        private exitCallback: () => Promise<void>
    ) {}

    exit(): Promise<void> {
        if (this._hasExited) {
            console.warn('cannot exit kiosk mode; exit was previously invoked')
            return;
        }

        console.debug('requesting to exit kiosk mode');

        this.exitCallback()
            .then(() => console.log('exited kiosk mode'))
            .catch(e => console.error('failed to exit kiosk mode', e));
    }
}
