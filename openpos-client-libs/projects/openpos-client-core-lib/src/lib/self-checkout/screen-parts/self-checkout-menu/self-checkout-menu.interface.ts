import { IActionItem } from '../../../core/interfaces/action-item.interface';


export interface SelfCheckoutMenuInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    headerIcon: string;
    backButton: IActionItem;
    skipButton: IActionItem;
    showScan: boolean;
    showAdmin: boolean;
    showLanguageSelector: boolean;
}
