import { Injectable } from '@angular/core';
import { Plugins as CapacitorPlugins, Capacitor } from '@capacitor/core';

import { Observable, of, throwError } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScanData } from '../../scanners/scan.interface';

import { ImageScanner, ImageScannerDef, ScannerViewRef } from '../image-scanner';

@Injectable() 
@ImageScannerDef('scandit-cap')
export class ScanditCapacitorImageScanner implements ImageScanner {
    constructor() {
        // todo: cheesing until bug in startup sequence can be resolved.
        if (Capacitor.isPluginAvailable('ScanditNative')) {
            CapacitorPlugins.ScanditNative.initialize({

                // todo: this is just a temporary trial key... needs to be changed when the initialization
                // is working. This is relaltively safe for now.
                apiKey: 'AbwvBxYVNq0xLhGzGTFChC4x3h3gN0BBB0V3vJl0U2C5ATpTdlNHcw53dF97M+vV3X33SgR1XGKZPHW8bVCJTx9ijAe8b+v4diwLtn91Hlx8WThJ4g4yihEUse/0JERz1UF5JudFXAFSIVpgblbJAygA9eUtbZF6BO7FApmFRgqfFFmwMvzx0LytU8gBV13FOlX8mqkEDKWZ/n3Wx2ChuMz9TE9wd/fZhkzwJS1+CHwm2hP2AW0DwotdB7ZFwUTJGt2NfkQhJIMt/k2dhVrLTRW+/rIrOqRqi3cm0gA0JwtxmiXVPnxYWUl8KG8aFDibFXJJCdSlwZvFyx8GbclYfCbsvI5kOBVrmDYsmrYzfL6LsuFQvDNPrTEoa47bxTrqvETWTugrwnBCwdT2UELrZ1v3NrbJrPq9gqrQT3hfYeTWqUSmfzwuq6/WVYJfRF0y9lnXvOwHxycQktjkzmS+rQHCWqtzTtkBhUEqnAcUPr7hALUeyNM8zE3m7+63C7etMptd7Pel7MgP7zlPvwZJPmDZmIngWbFRZrFcdncPAY76sl/Q99f6xEgVl+VHOJJk4AdibfUgFv6HYAtpNfv3fA1om7zrbA1owgJXEGQO4FCjUmvelnB9er8sbc2j4WVlaFjIeEZZqPqolmwKtoKHc+hpEFfLz586+qaJgri0eWdzIqtMb5eYBeYEX72mT8oq2uv5BxgEhDVlhD4MiadGkRNVxkDaNYuTGx0UGihjdYAuGZRLUB19NpiqqGlCu4luU8sWvt5iay0TUXa3CwAGWxJChe+2KFtMTS5B8w0dBkkLnw=='
            });
        }
    }

    beginScanning(view: ScannerViewRef): Observable<IScanData> {
        if (!Capacitor.isPluginAvailable('ScanditNative')) {
            // todo: cheesing until bug in startup sequence can be resolved.
            return of();

            return throwError('the scandit plugin is not available');
        }

        return new Observable(observer => {
            CapacitorPlugins.ScanditNative.addView();

            const updateViewSub = view.viewChanges().pipe(
                mergeMap(d => CapacitorPlugins.ScanditNative.updateView(d))
            ).subscribe();

            const handle = CapacitorPlugins.ScanditNative.addListener('scan', (e) => {
                observer.next({
                    type: e.symbology,
                    data: e.data
                });
            });

            return () => {
                handle.remove();
                updateViewSub.unsubscribe();
                CapacitorPlugins.ScanditNative.removeView();
            };
        });
    }
}
