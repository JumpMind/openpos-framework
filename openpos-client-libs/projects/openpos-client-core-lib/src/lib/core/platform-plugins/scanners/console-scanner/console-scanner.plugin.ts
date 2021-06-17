import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {IScanData} from '../scan.interface';
import {IScanner} from '../scanner.interface';

@Injectable({
    providedIn: 'root'
})
export class ConsoleScannerPlugin implements IScanner {

    scanSubject = new Subject<IScanData>();

    constructor() {
        console['scan'] = (value, type) => {
            this.scanSubject.next({data: value, type: type});
        }

        console['scanData'] = (scanData: IScanData) => {
            this.scanSubject.next({rawData: scanData.rawData, data: scanData.data, rawType: scanData.rawType, type: scanData.type });
        }
    }

    startScanning(): Observable<IScanData> {
        return this.scanSubject;
    }

    stopScanning() {
    }

    triggerScan() {
    }

}