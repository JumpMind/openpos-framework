
import { Inject, Injectable, Optional } from '@angular/core';
import { BehaviorSubject, merge, Observable, of, throwError } from 'rxjs';
import { share, switchMap } from 'rxjs/operators';
import { ConfigChangedMessage } from '../../messages/config-changed-message';
import { ConfigurationService } from '../../services/configuration.service';

import { IMAGE_SCANNERS, ImageScanner, ScannerViewRef, SCANNERS, Scanner, ScanData, ScanOptions } from './scanner';

@Injectable()
export class BarcodeScanner {
    get hasImageScanner(): boolean {
        return !!this._scanner.value;
    }

    get isImageScannerActive(): boolean {
        return !!this._activeScan;
    }

    private _scanner = new BehaviorSubject<ImageScanner | undefined>(undefined);
    private _activeScan?: Observable<ScanData>;

    constructor(
        configuration: ConfigurationService,
        @Inject(IMAGE_SCANNERS) @Optional() imageScanners?: ImageScanner[],
        @Inject(SCANNERS) @Optional() private _scanners?: Scanner[],
    ) {
        console.log('scanner config');
        configuration.getConfiguration('imageScanner').subscribe({
            next: (config: ConfigChangedMessage & any) => {
                console.log('got config for scanner');

                const type = config.scannerType as string;

                if (type && imageScanners) {
                    for (const scanner of imageScanners) {
                        const s = scanner as ImageScanner;
            
                        const configName = s.name();
    
                        if (type === configName) {
                            console.log(`using image scanner ${configName}`);
                            this._scanner.next(s);
                            return;
                        }
                    }
                }

                console.error(`could not find scanner with matching type \`${type}\``);
                this._scanner.next(undefined);
            }
        });
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
        
        // Sorta doing this weird wrapping in order to tack on some custom
        // teardown logic.
        this._activeScan = this._scanner.pipe(
            switchMap(scanner => new Observable<ScanData>(observer => {
                const sub = scanner.beginScanning(view).subscribe({
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
            }))
        )
        .pipe(
            // Multicast
            share()
        );

        return this._activeScan;
    }
}
