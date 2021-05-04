export interface DevicePersonalizationResponse {
    personalization: DevicePersonalization;
}

export interface DevicePersonalization {
    deviceName: string;
    serverAddress: string;
    serverPort: string;
    deviceId: string;
    appId: string;
    personalizationParams?: Map<string, string>;
    sslEnabled?: boolean;
}