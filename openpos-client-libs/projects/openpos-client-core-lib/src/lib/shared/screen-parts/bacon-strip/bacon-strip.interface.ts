import {IActionItem} from '../../../core/actions/action-item.interface';

export interface BaconStripInterface {
    operatorLine1: string;
    operatorLine2: string;
    headerText: string;
    logo: string;
    icon: string;
    actions: IActionItem[];
    operatorMenu: IActionItem[];
    operatorIcon: string;
}
