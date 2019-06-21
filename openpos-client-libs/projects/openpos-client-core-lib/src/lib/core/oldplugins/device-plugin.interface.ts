
import { IDeviceRequest } from './device-request.interface';
import { IOldPlugin } from './oldplugin.interface';
export interface IDevicePlugin extends IOldPlugin {
    processRequest(deviceRequest: IDeviceRequest, successCallback: (response: any) => void, errorCallback: (error: string) => void);
}
