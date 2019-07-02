import { Injectable, InjectionToken, Optional, Inject } from '@angular/core';
import { IScanner } from './scanner.interface';
import { Observable, merge } from 'rxjs';
import { IScanData } from './scan.interface';

export const SCANNERS = new InjectionToken<IScanner[]>('Scanners');

@Injectable()
export class ScannerService {
    constructor( @Optional() @Inject(SCANNERS) private scanners: Array<IScanner>) {
    }

    public startScanning(): Observable<IScanData> {
        return merge(...this.scanners.map(s => s.startScanning()));
    }

    public stopScanning() {
        this.scanners.forEach(s => s.stopScanning() );
    }
}
