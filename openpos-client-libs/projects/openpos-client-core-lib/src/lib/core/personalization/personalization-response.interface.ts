import {IDeviceModel} from './device-model.interface';

export interface PersonalizationResponse {
    authToken: string;
    deviceModel: IDeviceModel;
}