import { Injectable } from '@angular/core';

import { concat, Observable, of, throwError } from 'rxjs';
import { map, repeat } from 'rxjs/operators';

import { IPlatformPlugin } from '../../platform-plugin.interface';
import { Scanner, ScanData } from '../scanner';

import { AilaBarcodeUtils } from './aila-to-openpos-barcode-type';

@Injectable()
export class AilaScannerCordovaPlugin implements IPlatformPlugin, Scanner {
    private AilaCordovaPlugin;

    name(): string {
        return 'AilaCordovaPlugin';
    }

    pluginPresent(): boolean {
        return !!window.hasOwnProperty('AilaCordovaPlugin');
    }

    initialize(): Observable<string> {
        return Observable.create(observer => {
            // tslint:disable-next-line:no-string-literal
            this.AilaCordovaPlugin = window['AilaCordovaPlugin'];
            if ( !this.AilaCordovaPlugin ) {
                observer.error(`Tried to initialize plugin ${this.name()} which is not present`);
            }

            (this.AilaCordovaPlugin.setConfig(
                    true,
                    'PadlocScanScanningModeContinuous',
                    'PadlocScanMotionDetectionModeOn',
                    'PadlocScanBeepModeOn',
                    'PadlocScanCardDetectionModeOn',
                    ['PadlocScanCodeTypeUPCEAN', 'PadlocScanCodeTypeQR', 'PadlocScanCodeType128', 'PadlocScanCodeType39', 
                    'PadlocScanCodeType2of5', 'PadlocScanCodeTypePDF417', 'PadlocScanCodeTypeDataBar', 'PadlocScanCodeTypeDataMatrix', 
                    'PadlocScanCodeTypeCodaBar'],
                    'PadlocScanDebugLevelWarn') as Promise<any>)
                .then(this.AilaCordovaPlugin.init())
                .then(() => {
                     console.log('AilaCordovaPlugins initialized and configured successfully');
                     observer.complete();
                })
                .catch(error => {
                    console.log(`AilaCordovaPlugins failed to initialize ${error}`);
                    observer.error(error);
                });
        });
    }

    beginScanning(): Observable<ScanData> {
        if (!this.AilaCordovaPlugin) {
            return throwError(`Tried to start scanning with ${this.name()} which is not loaded`);
        }

        return concat(
            of(this.AilaCordovaPlugin.starScan()),
            new Observable<ScanData>(observer => {
                const dataSubscription = of(this.AilaCordovaPlugin.getNextScan()).pipe(
                    repeat(),
                    map(aila => {
                        const type = aila[0];
                        const data = aila[1];
        
                        return { type: AilaBarcodeUtils.convertToOpenposType(type, data.length), data };
                    })
                ).subscribe({
                    next: data => observer.next(data),
                    error: error => observer.error(error),
                });

                return () => {
                    dataSubscription.unsubscribe();
                    this.AilaCordovaPlugin.stopScanning();
                };
            })
        );
    }
}
