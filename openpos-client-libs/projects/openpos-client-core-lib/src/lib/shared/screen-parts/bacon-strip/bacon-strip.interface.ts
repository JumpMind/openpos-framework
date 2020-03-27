import {IActionItem} from '../../../core/actions/action-item.interface';

export interface BaconStripInterface {
    endSession: IActionItem;
    operatorLine1: string;
    operatorLine2: string;
    headerText: string;
    logo: string;
    icon: string;
    actions: IActionItem[];
}
