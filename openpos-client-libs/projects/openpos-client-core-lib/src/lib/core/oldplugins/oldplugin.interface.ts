export interface IOldPlugin {
    pluginId: string;
    pluginName?: string;
    impl: any;
    config?: any;

    configure?(params: any): boolean;
    init(successCallback: () => void, errorCallback: (error?: string) => void): void;
}
