import { Injectable } from '@angular/core';
import { IPlatformPlugin } from '../../../platform-plugin.interface';
import { IScanner } from '../../scanner.interface';
import { Observable, Subject } from 'rxjs';
import { IScanData } from '../../scan.interface';
import { InfineaBarcodeUtils } from '../infinea-to-openpos-barcode-type';

@Injectable({
    providedIn: 'root'
})
export class InfineaScannerCordovaPlugin implements IPlatformPlugin, IScanner {

    private infineaPlugin;
    private scanData$ = new Subject<IScanData>();

    name(): string {
        return 'InfineaScanner';
    }

    pluginPresent(): boolean {
        return !!window.hasOwnProperty('Infinea');
    }

    initialize(): Observable<string> {
        return Observable.create( (initialized: Subject<string>) => {
            // tslint:disable-next-line:no-string-literal
            this.infineaPlugin = window['Infinea'];

            this.infineaPlugin.barcodeDataCallback = (barcode, type, typeText) => {
                this.scanData$.next( { type: InfineaBarcodeUtils.convertToOpenposType(type), data: barcode});
            };

            initialized.next('Initializing Infinea Plugin');

            this.infineaPlugin.init(() => {
                initialized.next(`Infinea Plugin Initialized Success`);
                initialized.complete();
            }, () => {
                initialized.error(`Infinea Plugin Initialized Failure`);
            });
        });
    }

    startScanning(): Observable<IScanData> {
        /*this.infineaPlugin.barcodeScan(() => {
            console.log(`Infinea Enable Barcode Scan Success`);
        }, () => {
            console.log(`Infinea Enable Barcode Scan Failure`);
        }, 'true');*/

        return this.scanData$;
    }

    stopScanning() {
        /*this.infineaPlugin.barcodeScan(() => {
            console.log(`Time| ${new Date()} || Disable Barcode Scan| Success`);
        }, () => {
            console.log(`Time| ${new Date()} || Disable Barcode Scan| Failure`);
        }, 'false');*/
    }

    triggerScan() {
    }
  }
