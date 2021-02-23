declare module '@capacitor/core' {
    interface PluginRegistry {
        ScanditNative: ScanditPlugin;
    }
}

import { PluginListenerHandle } from '@capacitor/core';

export type ScanditEvents = 'scan';

export interface ScanditPlugin {
    initialize(args: InitializeArguments): Promise<void>;
    addView(args?: AddViewArguments): Promise<void>;
    removeView(args?: RemoveViewArguments): Promise<void>;
    updateView(constraints: ScanditViewConstraints): Promise<void>;

    addListener(event: 'scan', callback: (e: ScanditScanData) => void): PluginListenerHandle;
}

export type ScanditSymbology =
    'CODABAR' |
    'CODE39' |
    'CODE93' |
    'CODE128' |
    'CODE11' |
    'EAN8' |
    'GS1DATABAR' |
    'MSI_PLESSEY' |
    'PDF417' |
    'MICROPDF417' |
    'DATAMATRIX' |
    'AZTEK' |
    'QRCODE' |
    'MAXICODE' |
    'UPCE' |
    'MATRIX_2OF5' |
    'UPCA';

export interface ScanditScanData {
    symbology: ScanditSymbology;
    data: string;
}

export interface InitializeArguments {
    apiKey: string
}

export interface AddViewArguments {}

export interface RemoveViewArguments {}

export interface ScanditViewConstraints {
    left: number;
    top: number;
    width: number;
    height: number;
}
