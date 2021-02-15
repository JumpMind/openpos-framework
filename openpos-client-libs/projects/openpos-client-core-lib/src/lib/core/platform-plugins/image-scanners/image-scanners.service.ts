
import { Inject, Injectable, Optional } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { share, takeWhile } from 'rxjs/operators';

import { IMAGE_SCANNERS, ImageScanner, ScannerViewRef } from './image-scanner';

import { IScanData } from '../scanners/scan.interface';

@Injectable()
export class ImageScanners {
    get isSupported(): boolean {
        return !!this._scanner;
    }

    get isScanning(): boolean {
        return !!this._activeScan;
    }

    private _scanner?: ImageScanner;
    private _activeScan?: Observable<IScanData>;

    constructor(
       @Inject(IMAGE_SCANNERS) @Optional() scanners: ImageScanner[]
    ) {
        if (!scanners) {
            return;
        }

        for (const scanner of scanners) {
            const s = scanner as ImageScanner;

            const configName = s.name();

            console.log(`using image scanner ${configName}`);

            // just selecting the first one for now...
            this._scanner = s;

            return;

            // todo: need to select based on configuration...
        }
    }

    beginScanning(view: ScannerViewRef): Observable<IScanData> {
        if (!this.isSupported) {
            return throwError('no image scanner is supported');
        }

        if (this.isScanning) {
            return throwError('only one active scan allowed at a time');
        }

        let captureScanner = this._scanner;
        
        // Sorta doing this weird wrapping in order to tack on some custom
        // teardown logic.
        this._activeScan = new Observable<IScanData>(observer => {
            const sub = this._scanner.beginScanning(view).subscribe({
                next: e => {
                    observer.next(e);
                },
                error: e => {
                    observer.error(e);
                },
                complete: () => {
                    observer.complete();
                }
            });

            return () => {
                sub.unsubscribe();
                this._activeScan = undefined;
            };
        }).pipe(
            // Complete this sequence if the scanner was changed from when we
            // started.
            takeWhile(() => this._scanner === captureScanner),

            // Multicast
            share()
        );

        return this._activeScan;
    }
}
