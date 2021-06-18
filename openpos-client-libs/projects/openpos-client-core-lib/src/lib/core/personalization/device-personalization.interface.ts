export interface AutoPersonalizationParametersResponse {
    deviceName: string;
    serverAddress: string;
    serverPort: string;
    deviceId: string;
    appId: string;
    personalizationParams?: any;
    sslEnabled?: boolean;
}