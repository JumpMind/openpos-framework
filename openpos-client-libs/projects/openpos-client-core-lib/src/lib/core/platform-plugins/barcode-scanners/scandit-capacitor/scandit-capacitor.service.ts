import { Injectable } from '@angular/core';
import { Plugins as CapacitorPlugins, Capacitor } from '@capacitor/core';

import { Observable, of, throwError } from 'rxjs';
import { catchError, map, mergeMap, switchMap, take, timeout } from 'rxjs/operators';
import { ConfigChangedMessage } from '../../../messages/config-changed-message';
import { ConfigurationService } from '../../../services/configuration.service';
import { IPlatformPlugin } from '../../platform-plugin.interface';

import { ImageScanner, ScannerViewRef, ScanData } from '../scanner';

@Injectable({
    providedIn: 'root'
})
export class ScanditCapacitorImageScanner implements ImageScanner, IPlatformPlugin {
    private _initializedWithError = false;

    constructor(private _config: ConfigurationService) {}

    name(): string {
        return 'scandit-cap';
    }
    
    pluginPresent(): boolean {
        return Capacitor.isPluginAvailable('ScanditNative');
    }

    initialize(): Observable<string> {
        return this._config.getConfiguration('ScanditCapacitor').pipe(
            take(1),
            timeout(10000),
            switchMap((config: ConfigChangedMessage & any) => {
                if (config.licenseKey) {
                    return of(CapacitorPlugins.ScanditNative.initialize({
                        apiKey: config.licenseKey
                    }));
                }

                return throwError('could not find Scandit license key');
            }),
            map(() => "initialized Scandit for Capacitor"),
            catchError(() => {
                this._initializedWithError = true;
                return of("failed to start scandit; will disable")
            })
        );
    }

    beginScanning(view: ScannerViewRef): Observable<ScanData> {
        if (!Capacitor.isPluginAvailable('ScanditNative')) {
            return throwError('the scandit plugin is not available');
        }

        if (this._initializedWithError) {
            return of();
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
