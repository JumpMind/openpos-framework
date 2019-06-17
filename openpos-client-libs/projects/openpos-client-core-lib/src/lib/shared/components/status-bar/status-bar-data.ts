import { IActionItem } from '../../../core/interfaces/action-item.interface';



export class StatusBarData {
    backButton: IActionItem;
    screenType: String;
    screenName: String;
    screenIcon: String;
    operatorText: String;
    logoutButton: IActionItem;
    workstationId: String;
    storeId: String;
    tillThresholdStatus: number;
    trainingEnabled: boolean;
    enableHomeAction = true;
}
