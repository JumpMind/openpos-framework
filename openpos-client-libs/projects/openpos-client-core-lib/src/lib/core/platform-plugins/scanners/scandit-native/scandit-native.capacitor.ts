declare module '@capacitor/core' {
    interface PluginRegistry {
        ScanditNative: ScanditPlugin;
    }
}
  
export interface ScanditPlugin {
    initialize(args: InitializeArguments): Promise<void>;
    addView(args?: AddViewArguments): Promise<void>;
    removeView(args?: RemoveViewArguments): Promise<void>;
    updateView(constraints: ScanditViewConstraints): Promise<void>;
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
