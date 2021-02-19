import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { Scanner, ScanData, ScanDataType } from '../image-scanner';

declare global {
    interface Console {
        scan?: (type: ScanDataType, data: string) => void;
    }
}

@Injectable({
    providedIn: 'root'
})
export class ConsoleScannerPlugin implements Scanner {
    scanSubject = new Subject<ScanData>();

    constructor() {
        console.scan = (type, value) => {
            this.scanSubject.next({
                type: type, 
                data: value
            });
        };
    }

    beginScanning(): Observable<ScanData> {
        return this.scanSubject.asObservable();
    }
}
