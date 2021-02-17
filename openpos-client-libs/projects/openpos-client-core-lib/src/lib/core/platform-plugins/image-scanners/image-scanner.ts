import { InjectionToken } from '@angular/core';

import { Observable } from 'rxjs';
import { IScanData } from '../scanners/scan.interface'

export const IMAGE_SCANNERS = new InjectionToken<ImageScanner[]>('ImageScanners');

export interface ScannerViewRef {
    viewChanges(): Observable<{ left: number; top: number; width: number, height: number }>;
}

export interface ImageScanner {
    name(): string;

    /**
     * Starts the image scanner and returns a hot observable which yields
     * scan results. The image scanner must be closed on dispose.
     */
    beginScanning(view: ScannerViewRef): Observable<IScanData>; 
}

export function ImageScannerDef<T extends { new (...args: any[]) }>(configSelector: string) {
    return (ctor: T) => {
        return class extends ctor {
            get configSelector(): string {
                return configSelector;
            }
        }
    };
}
