import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';

import { Plugins } from '@capacitor/core';

import { IScanner } from '../../scanners/scanner.interface';
import { IScanData } from '../scan.interface';

@Injectable({
    providedIn: 'root'
})
export class ScanditNativeScanner implements IScanner {
    _scanResult = new Subject<IScanData>();

    constructor() {
        // ScanditNative.addListener('scan', (info: any) => {
        //     this._scanResult.next({
        //         type: info.symbology,
        //         data: info.data
        //     });
        // });

        
    }
    
    startScanning(): Observable<IScanData> {
        return this._scanResult;
    }

    stopScanning() {
    }

    triggerScan() {
    }
}