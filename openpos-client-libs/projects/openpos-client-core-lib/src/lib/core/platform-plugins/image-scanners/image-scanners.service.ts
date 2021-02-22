
import { Inject, Injectable, Optional } from '@angular/core';
import { merge, Observable, of, throwError } from 'rxjs';
import { share, takeWhile, tap } from 'rxjs/operators';

import { IMAGE_SCANNERS, ImageScanner, ScannerViewRef, SCANNERS, Scanner, ScanData, ScanOptions } from './scanner';

@Injectable()
export class ImageScanners {
    get hasImageScanner(): boolean {
        return !!this._scanner;
    }

    get isImageScannerActive(): boolean {
        return !!this._activeScan;
    }

    private _scanner?: ImageScanner;
    private _activeScan?: Observable<ScanData>;

    constructor(
       @Inject(IMAGE_SCANNERS) @Optional() imageScanners?: ImageScanner[],
       @Inject(SCANNERS) @Optional() private _scanners?: Scanner[],
    ) {
        if (!imageScanners) {
            return;
        }

        for (const scanner of imageScanners) {
            const s = scanner as ImageScanner;

            const configName = s.name();

            console.log(`using image scanner ${configName}`);

            // just selecting the first one for now...
            this._scanner = s;

            return;

            // todo: need to select based on configuration...
        }
    }

    beginScanning(options?: ScanOptions): Observable<ScanData> {
        if (!this._scanners) {
            return of();
        }

        return merge(
            ...this._scanners.map(s => s.beginScanning(options))
        );
    }

    beginImageScanning(view: ScannerViewRef): Observable<ScanData> {
        if (!this.hasImageScanner) {
            return throwError('no image scanner is supported');
        }

        if (this.isImageScannerActive) {
            return throwError('only one active scan allowed at a time');
        }

        let captureScanner = this._scanner;
        
        // Sorta doing this weird wrapping in order to tack on some custom
        // teardown logic.
        this._activeScan = new Observable<ScanData>(observer => {
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
