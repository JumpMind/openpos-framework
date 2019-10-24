import { IActionItem } from '../../../../core/interfaces/action-item.interface';

/**
 * @ignore
 */
export interface IStatusBar {
    backButton: IActionItem;
    skipButton: IActionItem;
    operatorText: String;
    logoutButton: IActionItem;
    workstationId: String;
    showScan: boolean;
    showAdmin: boolean;
    showLanguageSelector: boolean;
    enableHomeAction: boolean;
}
