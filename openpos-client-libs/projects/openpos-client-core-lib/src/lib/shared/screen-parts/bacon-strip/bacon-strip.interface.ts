import {IActionItem} from '../../../core/actions/action-item.interface';

export interface BaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    version: string;
    certification: string;
    logo: string;
    icon: string;
    actions: IActionItem[];
}
