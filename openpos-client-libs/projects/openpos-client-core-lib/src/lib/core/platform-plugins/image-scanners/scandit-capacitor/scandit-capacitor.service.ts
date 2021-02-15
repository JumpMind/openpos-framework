import { Injectable } from '@angular/core';
import { Plugins as CapacitorPlugins, Capacitor } from '@capacitor/core';

import { Observable, of, throwError } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { IPlatformPlugin } from '../../platform-plugin.interface';

import { IScanData } from '../../scanners/scan.interface';

import { ImageScanner, ScannerViewRef } from '../image-scanner';

@Injectable({
    providedIn: 'root'
}) 
//@ImageScannerDef('scandit-cap')
export class ScanditCapacitorImageScanner implements ImageScanner {
    name(): string {
        return 'scandit-cap';
    }
    
    pluginPresent(): boolean {
        return Capacitor.isPluginAvailable('ScanditNative');
    }

    initialize(): Observable<string> {
        return of(CapacitorPlugins.ScanditNative.initialize({
            // todo: this is just a temporary trial key... needs to be changed when the initialization
            // is working. This is relaltively safe for now.
            apiKey: 'AQ7PciYVBjPsPMwycQwa+vMNI7nFMbWe6n8qGeVOH6hwVTOdj23ueJc/ndhZO4SHuAtJs8hiTZEWS6zLvkm6ku4KooIxTeY0qQ2k8fhECQeoR356lEOJcfJlxzvxaax2/3Q+S61uYkjjI4bf0wuziWQ9VDfTJRJoMkooMUH3cMjo7iGRospzN6MBp2xOFlCBVGe44143LAWRSvtYZd2pn2+yCRl72aIbV+/HfzNsrDK20bN+TEwvJ2TYeHjGFr742+LrfKdREIPyFBv3lf6yHlUP9ZYJCviXLAJwPnjDN088y8gFZT6a4ahtr8ExiNCkIFLKPsw8j1GwA32tCZfLf/BljadFyHe0bAhpNQEypZJ7MfaVx5lC+NGvV06m4+QqpYVhdA4jdVQw4l6vT17gOaaKPb4e3VFV1Mm5EbV/jeZgJGGUZ3nGeJ03twYIcLyVb1dfhZWBUUQzK2pbco040hbmE5xQk278p18j5OpJEoXKnkt2FO7saAWdfYt+pqCmTaAQzPwHtlsuJlNfJ9lI8cpnBaDToB9IqkpMaP6Rfeg2O3cAUCq0+l07e4SmVFPbDIeKuTd+9F9hmQPq+RScK21k6KTaRSKzph2REJDk4pUm83mkDoxTLUpJrAyRxVrTKnVdxAdfWNT1I9b8Tg2HH6JCK2EuQpjVUT9JCf6W1hz9x8et/jlwAT884Dy+z1UrbD5c88qhsiQTHmwDHvJtMtVSi4totqfNkmyd+4IAGAd+i+mbjYpFQ8FZdkl9EU5knBxHVkKK5591aPbnu0eCQTfBrka3zJciphL8J5NACPN//6HvQoJ1+yHEknnr6Rkwj8sfrs/Y/FzP'
        })).pipe(
            map(() => "Initialized Scandit for Capacitor")
        );
    }

    beginScanning(view: ScannerViewRef): Observable<IScanData> {
        if (!Capacitor.isPluginAvailable('ScanditNative')) {
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
