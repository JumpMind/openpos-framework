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
    'CODE128';

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
