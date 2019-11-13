import { IActionItem } from '../../../core/actions/action-item.interface';
import { SearchExpandInputInterface } from '../search-expand-input/search-expand-input.interface';

export interface BaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    logo: string;
    actions: IActionItem[];
}
