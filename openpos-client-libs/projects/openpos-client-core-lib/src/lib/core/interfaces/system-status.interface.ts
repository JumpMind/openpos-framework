import { SystemStatusType } from './system-status-type.enum';
import { IDevice } from './device.interface';

export interface ISystemStatus {
    overallSystemStatus: SystemStatusType;
    devices: IDevice[];
}
