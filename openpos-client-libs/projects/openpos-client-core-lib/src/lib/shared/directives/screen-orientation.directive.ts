import { Logger } from './../../core/services/logger.service';
import { Directive, Input, OnDestroy, OnInit } from '@angular/core';
import { DeviceService } from '../../core/services/device.service';

export const MODE_LOCK_CURRENT = 'lock-current';
export const MODE_PORTRAIT = 'portrait';
export const MODE_LANDSCAPE = 'landscape';
export const MODE_PORTRAIT_PRIMARHY = 'portrait-primary';
export const MODE_LANDSCAPE_PRIMARY = 'landscape-primary';
export const MODE_PORTRAIT_SECONDARY = 'portrait-secondary';
export const MODE_LANDSCAPE_SECONDARY = 'landscape-secondary';

// See https://github.com/apache/cordova-plugin-screen-orientation for explanation of modes
export type OrientationMode = 'lock-current' | 'portrait' | 'portrait-primary' | 'portrait-secondary'
   | 'landscape' | 'landscape-primary' | 'landscape-secondary';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[screenOrientation]'
})
export class ScreenOrientationDirective implements OnInit, OnDestroy {

    private _enabled = false;
    @Input('screenOrientation') orientationMode: OrientationMode;

    constructor(private log: Logger, private deviceService: DeviceService) {
    }


    ngOnInit(): void {
        this.deviceService.onDeviceReady.subscribe(message => {
            if (message && this.deviceService.isRunningInCordova()) {
                if (window.screen && (<any>window.screen).orientation) {
                    this._enabled = true;
                    const currentOrientation = (<any>window.screen).orientation.type;
                    this.log.info(`Current screen orientation is: ${currentOrientation}`);
                    this.log.info(`Requested orientationMode is: ${this.orientationMode}`);
                    if (this.orientationMode) {
                        switch (this.orientationMode) {
                            case MODE_LOCK_CURRENT:
                                (<any>window.screen).orientation.lock(currentOrientation);
                                this.log.info(`Locking orientation to: ${currentOrientation}`);
                                break;
                            default:
                                (<any>window.screen).orientation.lock(this.orientationMode);
                                this.log.info(`Locking orientation to: ${this.orientationMode}`);
                        }
                    }
                }
            }
         });
    }

    ngOnDestroy(): void {
        if (this._enabled) {
            this.log.info('Unlocking orientation');
            (<any>window.screen).orientation.unlock();
        }
    }

}
