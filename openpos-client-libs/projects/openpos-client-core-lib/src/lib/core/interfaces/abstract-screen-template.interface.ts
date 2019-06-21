import { ISystemStatus } from './system-status.interface';

// tslint:disable-next-line:no-empty-interface
export interface IAbstractScreenTemplate {
    type: string;
    dialog: boolean;
    systemStatus: ISystemStatus;
}
