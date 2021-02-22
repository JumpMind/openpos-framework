import { Injectable } from '@angular/core';
import { IPlatformPlugin } from '../../platform-plugin.interface';
import { Observable, Subject } from 'rxjs';
import { InfineaBarcodeUtils } from './infinea-to-openpos-barcode-type';
import { ScanData, Scanner } from '../scanner';

@Injectable({
    providedIn: 'root'
})
export class InfineaScannerCordovaPlugin implements IPlatformPlugin, Scanner {

    private infineaPlugin;
    private scanData$ = new Subject<ScanData>();

    name(): string {
        return 'InfineaScanner';
    }

    pluginPresent(): boolean {
        return !!window.hasOwnProperty('Infinea');
    }

    beginScanning(): Observable<ScanData> {
        return this.scanData$;
    }

    initialize(): Observable<string> {
        return new Observable(observer => {
            // tslint:disable-next-line:no-string-literal
            this.infineaPlugin = window['Infinea'];

            this.infineaPlugin.barcodeDataCallback = (barcode, type, typeText) => {
                this.scanData$.next( { type: InfineaBarcodeUtils.convertToOpenposType(type), data: barcode});
            };

            observer.next('Initializing Infinea Plugin');

            this.infineaPlugin.init(() => {
                observer.next(`Infinea Plugin Initialized Success`);
                observer.complete();
            }, () => {
                observer.error(`Infinea Plugin Initialized Failure`);
            });
        });
    }
  }
