import { IActionItem } from '../../../core/interfaces/action-item.interface';


export interface BaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    headerIcon: string;
    backButton: IActionItem;
    deviceIdCss: string;
}
