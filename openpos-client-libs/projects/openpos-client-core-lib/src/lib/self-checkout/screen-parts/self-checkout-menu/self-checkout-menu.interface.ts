import { IActionItem } from '../../../core/actions/action-item.interface';


export interface SelfCheckoutMenuInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    headerIcon: string;
    backButton: IActionItem;
    skipButton: IActionItem;
    lightButton: IActionItem;
    showScan: boolean;
    showAdmin: boolean;
    showLanguageSelector: boolean;
    logo: string;
}
