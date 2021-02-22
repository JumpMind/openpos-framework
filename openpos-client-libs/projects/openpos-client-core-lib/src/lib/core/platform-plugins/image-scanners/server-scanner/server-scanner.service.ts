import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { SessionService } from '../../../services/session.service';
import { ScanData, Scanner } from '../scanner';

@Injectable({
    providedIn: 'root',
})
export class ServerScannerPlugin implements Scanner {
    constructor(private sessionService: SessionService) {}

    beginScanning(): Observable<ScanData> {
        return this.sessionService.getMessages('Scan').pipe(
            map(m => m.scanData)
        );
    }
}
