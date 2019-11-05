import { IActionItem } from '../../../core/actions/action-item.interface';
import { SearchExpandInputInterface } from '../../components/search-expand-input/search-expand-input.interface';

export interface BaconStripInterface {
    deviceId: string;
    operatorText: string;
    headerText: string;
    headerIcon: string;
    logo: string;
    actions: IActionItem[];
    searchBar: SearchExpandInputInterface;
}
