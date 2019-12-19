import { IPlatformPlugin } from '../../platform-plugin.interface';
import { IScanner } from '../scanner.interface';
import { Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { IScanData } from '../scan.interface';
import { AilaBarcodeUtils } from './aila-to-openpos-barcode-type';

@Injectable()
export class AilaScannerCordovaPlugin implements IPlatformPlugin, IScanner {

    private scanData$ = new Subject<IScanData>();

    private AilaCordovaPlugin;

    name(): string {
        return 'AilaCordovaPlugin';
    }

    pluginPresent(): boolean {
        return !!window.hasOwnProperty('AilaCordovaPlugin');
    }

    initialize(): Observable<string> {
        return Observable.create( (initialized: Subject<string>) => {
            // tslint:disable-next-line:no-string-literal
            this.AilaCordovaPlugin = window['AilaCordovaPlugin'];
            if ( !this.AilaCordovaPlugin ) {
                initialized.error(`Tried to initialize plugin ${this.name()} which is not present`);
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
                .then(result => {
                     console.log('AilaCordovaPlugins initialized and configured successfully');
                     initialized.complete();
                })
                .catch(error => {
                    console.log(`AilaCordovaPlugins failed to initialize ${error}`);
                    initialized.error(error);
                });
        });
    }

    startScanning(): Observable<IScanData> {
        if ( !this.AilaCordovaPlugin ) {
            throw Error(`Tried to start scanning with ${this.name()} which is not loaded`);
        }

        (this.AilaCordovaPlugin.scanStart() as Promise<any>)
            .then(result => this.seqScanStep())
            .catch(error => console.log(`Aila scanStart Error ${error}`));
        return this.scanData$;
    }

    stopScanning() {
        if ( !this.AilaCordovaPlugin ) {
            throw Error(`Tried to stop scanning with ${this.name()} which is not loaded`);
        }

        (this.AilaCordovaPlugin.scanStop() as Promise<any>)
            .catch(error => {
                console.log(`Aila scanStop() Error: ${error}`); 
                alert('Error: ' + error);
            });
    }

    private seqScanStep() {
        (this.AilaCordovaPlugin.getNextScan() as Promise<any>)
            .then(result => {
                console.log(`Aila Scan raw result ${JSON.stringify(result)}`);
                const type = result[0];
                const data = result[1];
                console.log( `Aila Scan Type: ${type}`);
                console.log( `Aila Scan Data: ${data}`);
                this.scanData$.next({type: AilaBarcodeUtils.convertToOpenposType(type, data.length), data});
                this.seqScanStep();
            })
            .catch(error => {
                console.log(`Aila getNextScan() Error: ${error}`);
                alert('Error: ' + error);
            });
    }
}
