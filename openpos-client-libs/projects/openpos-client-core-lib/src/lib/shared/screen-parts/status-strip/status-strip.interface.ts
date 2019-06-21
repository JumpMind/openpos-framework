import { ISystemStatus } from '../../../core/interfaces/system-status.interface';

export interface StatusStripInterface {
    storeNumber: string;
    registerNumber: string;
    systemStatus: ISystemStatus;
    timestampBegin: number;
}
