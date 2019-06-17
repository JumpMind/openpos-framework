export const DEVICE_RESPONSE_TYPE = 'DeviceResponse';
export const DEVICE_ERROR_RESPONSE_TYPE = 'DeviceErrorResponse';
export const DEVICE_TIMEOUT_RESPONSE_TYPE = 'DeviceTimeoutResponse';
export const  DEVICE_DNE_RESPONSE_TYPE = 'DeviceDoesNotExistResponse';

export interface IDeviceResponse {
    requestId: string;
    deviceId: string;
    type: string;
    payload: string;
}
