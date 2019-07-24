import { IActionItem } from '../../../core/actions/action-item.interface';


export interface SelfCheckoutMenuInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    headerIcon: string;
    backButton: IActionItem;
    showScan: boolean;
    showAdmin: boolean;
    showSkip: boolean;
    showLanguageSelector: boolean;
}
