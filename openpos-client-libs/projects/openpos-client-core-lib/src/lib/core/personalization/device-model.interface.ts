import {IDeviceParamModel} from './device-param-model.interface';

export interface  IDeviceModel {
    deviceId: string;
    appId: string;
    deviceParamModels: IDeviceParamModel[];
}