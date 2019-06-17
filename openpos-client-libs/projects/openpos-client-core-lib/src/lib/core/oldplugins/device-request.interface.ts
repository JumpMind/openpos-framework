export interface IDeviceRequest {
    pluginId?: string;
    requestId: string;
    deviceId: string;
    type: string;
    subType: string;
    payload: any;
}
