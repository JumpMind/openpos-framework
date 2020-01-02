import { IActionItem } from '../../../core/actions/action-item.interface';

export interface BasicBaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    logo: string;
    actions: IActionItem[];
    icon: string;
}
