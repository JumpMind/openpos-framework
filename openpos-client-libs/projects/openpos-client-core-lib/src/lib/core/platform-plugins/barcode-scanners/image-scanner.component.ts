import { Component, Output, EventEmitter, OnDestroy, OnInit, ElementRef } from '@angular/core';
import { interval, merge, Observable, Subject, Subscription, throwError } from 'rxjs';
import { delay, distinctUntilChanged, map, takeWhile, tap } from 'rxjs/operators';

import { ScanData, ScannerViewRef } from './scanner';

import { BarcodeScanner } from './barcode-scanner.service';

@Component({
    selector: 'app-image-scanner',
    template: '',
    styles: [
        `
        :host {
            width: 100%;
            height: 100%;
        }
        `
    ]
})
export class ImageScannerComponent implements OnInit, OnDestroy, ScannerViewRef {
    @Output() readonly scanChanged = new EventEmitter<boolean>();
    @Output() readonly onScan = new EventEmitter<ScanData>();

    get element(): HTMLElement {
        return this._elementRef.nativeElement;
    }

    private _scanSubscription?: Subscription;

    private _viewChanges?: Observable<{ left: number; top: number; width: number; height: number; }>;

    private _destroyed = true;

    constructor(
        private _elementRef: ElementRef<HTMLElement>,
        private _scanners: BarcodeScanner
    ) { }

    ngOnInit() {
        this._destroyed = false;

        const viewChanges = [
            ImageScannerComponent._makeObservableListener(
                cb => window.addEventListener('orientationchange', cb),
                cb => window.removeEventListener('orientationchange', cb)
            ).pipe(
                // orientation changes get funky; delay them a little.
                delay(1000)
            ),
            interval(500).pipe(
                map(() => {
                    const box = this._elementRef.nativeElement.getBoundingClientRect();

                    return {
                        left: box.left,
                        top: box.top,
                        width: box.width,
                        height: box.height
                    };
                }),
                distinctUntilChanged((x, y) => 
                    x.left === y.left
                    && x.top === y.top
                    && x.width === y.width
                    && x.height === y.height
                )
            )
        ];

        let currentElement: HTMLElement = this._elementRef.nativeElement;
        while (currentElement !== document.body) {
            currentElement = ImageScannerComponent._getScrollParent(currentElement, true);
            viewChanges.push(ImageScannerComponent._makeObservableListener(
                (cb) => currentElement.addEventListener('scroll', cb),
                (cb) => currentElement.removeEventListener('scroll', cb)
            ));
        }

        this._viewChanges = merge(...viewChanges).pipe(
            takeWhile(() => !this._destroyed),
            map(() => {
                const box = this._elementRef.nativeElement.getBoundingClientRect();

                return {
                    left: box.left,
                    top: box.top,
                    width: box.width,
                    height: box.height
                };
            })
        );

        this._scanSubscription = this._scanners.beginImageScanning(this)
            .subscribe({
                next: data => {
                    this.onScan.emit(data);
                },
                error: e => {
                    console.log('unexpected error durring image scanning', e);

                    this._scanSubscription = undefined;
                    this.scanChanged.emit(false);
                },
                complete: () => {
                    this._scanSubscription = undefined;
                    this.scanChanged.emit(false);
                }
            });
    }

    ngOnDestroy() {
        if (this._scanSubscription) {
            this._scanSubscription.unsubscribe();
        }

        this._destroyed = true;
    }

    viewChanges(): Observable<{ left: number; top: number; width: number; height: number; }> {
        if (!this._viewChanges) {
            return throwError('not initialized');
        }

        return this._viewChanges;
    }

    private static _makeObservableListener(
        subscribe: (e: () => void) => void,
        unsubscribe: (e: () => void) => void
    ): Observable<void> {
        return new Observable(observer => {
            const callbackFn = () => {
                observer.next();
            };

            subscribe(callbackFn);

            return () => {
                unsubscribe(callbackFn);
            };
        });
    }

    private static _getScrollParent(element: HTMLElement, includeHidden: boolean): HTMLElement {
        var style = getComputedStyle(element);
        var excludeStaticParent = style.position === "absolute";
        var overflowRegex = includeHidden ? /(auto|scroll|hidden)/ : /(auto|scroll)/;
    
        if (style.position === "fixed") return document.body;
        for (var parent = element; (parent = parent.parentElement);) {
            style = getComputedStyle(parent);
            if (excludeStaticParent && style.position === "static") {
                continue;
            }
            if (overflowRegex.test(style.overflow + style.overflowY + style.overflowX)) return parent;
        }
    
        return document.body;
    }
}
