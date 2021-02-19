import { InjectionToken } from '@angular/core';

import { Observable } from 'rxjs';

export const SCANNERS = new InjectionToken<Scanner[]>('Scanners');
export const IMAGE_SCANNERS = new InjectionToken<ImageScanner[]>('ImageScanners');

export interface ScannerViewRef {
    viewChanges(): Observable<{ left: number; top: number; width: number, height: number }>;
}

export interface ScanData {
    rawType?: string;
    type?: ScanDataType;
    data: string;
    rawData?: string;
}

export interface ScanOptions {
    /**
     * Emits during scanning operations and informs scanners who have a
     * software trigger when they should act.
     */
    softwareTrigger?: Observable<void>;
}

export interface Scanner {
    /**
     * Starts the scanner and returns an observable which yields scan 
     * results.
     */
    beginScanning(options: ScanOptions): Observable<ScanData>;
}

export interface ImageScanner {
    name(): string;

    /**
     * Starts the image scanner and returns a hot observable which yields
     * scan results. The image scanner must be closed on dispose.
     */
    beginScanning(view: ScannerViewRef): Observable<ScanData>; 
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

export type ScanDataType = 
    'UPCA' |
    'CODABAR' |
    'CODE25_NI2OF5' |
    'CODE25_I2OF5' |
    'CODE39' |
    'CODE93' |
    'CODE128' |
    'CODE11' |
    'CPCBINARY' |
    'DUN14' |
    'EAN2' |
    'EAN5' |
    'EAN8' |
    'EAN13' |
    'EAN128' |
    'GS1DATABAR' |
    'ITF14' |
    'LATENT_IMAGE' |
    'PHARMACODE' |
    'PLANET' |
    'POSTNET' |
    'INTELLIGENT_MAIL' |
    'MSI_PLESSEY' |
    'POSTBAR' |
    'RM4SCC' |
    'TELEPEN' |
    'UK_PLESSEY' |
    'PDF417' |
    'MICROPDF417' |
    'DATAMATRIX' |
    'AZTEK' |
    'QRCODE' |
    'MAXICODE' |
    'UPCA_2' |
    'UPCA_5' |
    'UPCE' |
    'UPCE_2' |
    'UPCE_5' |
    'UPC_UNKNOWN' |
    'EAN13_2' |
    'EAN13_5' |
    'EAN8_2' |
    'EAN8_5' |
    'CODE39_FULL' |
    'ITA_PHARMA' |
    'CODABAR_ABC' |
    'CODABAR_CX' |
    'SCODE' |
    'MATRIX_2OF5' |
    'IATA' |
    'KOREAN_POSTAL' |
    'CCA' |
    'CCB' |
    'CCC' |
    'ITF';
