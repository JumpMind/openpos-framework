import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { IScanner } from '../scanner.interface';
import { IScanData } from '../scan.interface';
import { SessionService } from '../../../services/session.service';

@Injectable({
    providedIn: 'root',
})
export class ServerScannerPlugin implements IScanner {

    scanSubject = new Subject<IScanData>();

    constructor(private sessionService: SessionService) {
        console.log('Subscribing to Server Scan messages...');
        this.sessionService.getMessages('Scan').subscribe(message => this.handleJPosScan(message));
    }

    handleJPosScan(message) {
        this.scanSubject.next(message.scanData);
    }

    startScanning(): Observable<IScanData> {
        return this.scanSubject;
    }

    stopScanning() { }

    triggerScan() { }

}
