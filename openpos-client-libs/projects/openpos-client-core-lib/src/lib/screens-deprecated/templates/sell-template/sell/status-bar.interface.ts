import { IActionItem } from '../../../../core/interfaces/action-item.interface';

/**
 * @ignore
 */
export interface IStatusBar {
    backButton: IActionItem;
    operatorText: String;
    logoutButton: IActionItem;
    workstationId: String;
    showScan: boolean;
    showAdmin: boolean;
    showSkip: boolean;
    showLanguageSelector: boolean;
    enableHomeAction: boolean;
}
