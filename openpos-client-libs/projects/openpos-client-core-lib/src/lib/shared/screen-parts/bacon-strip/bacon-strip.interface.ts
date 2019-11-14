import { IActionItem } from '../../../core/actions/action-item.interface';

export interface BaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    logo: string;
    actions: IActionItem[];
}
