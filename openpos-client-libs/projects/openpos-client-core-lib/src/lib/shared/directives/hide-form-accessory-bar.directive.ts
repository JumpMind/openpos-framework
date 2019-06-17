import { Directive, OnDestroy } from '@angular/core';
import { CordovaService } from '../../core/services/cordova.service';

// Directive used to hide the Form Accessory Bar on mobile devices.
// Requires cordova-plugin-keyboard to be installed.
@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[hideFormAccessoryBar]'
})
export class HideFormAccessoryBarDirective implements OnDestroy {

    constructor(private cordovaService : CordovaService) {
        this._toggleFormBar(true);
    }

    ngOnDestroy() : void {
        this._toggleFormBar(false);
    }

    private _toggleFormBar(toggle: boolean) : void {
        const windowRef = window as any;
        if (this.cordovaService.isRunningInCordova() && windowRef.Keyboard && typeof windowRef.Keyboard.hideFormAccessoryBar === 'function') {
            windowRef.Keyboard.hideFormAccessoryBar(toggle);
        }
    }
}