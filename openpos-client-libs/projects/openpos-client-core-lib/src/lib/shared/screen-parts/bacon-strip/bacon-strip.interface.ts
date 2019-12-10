import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { TillStatusType } from '../../../core/interfaces/till-status-type.enum';


export interface BaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    headerIcon: string;
    backButton: IActionItem;
    tillStatusType: TillStatusType;
}
