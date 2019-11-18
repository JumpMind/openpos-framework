import {IActionItem} from '../../core/actions/action-item.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { DisplayProperty } from '../../shared/components/display-property/display-property.interface';

export interface ItemDetailInterface extends IAbstractScreen {
    imageUrls: string[];
    itemName: string;
    summary: string;
    itemProperties: DisplayProperty[];
    itemActions: IActionItem[];
}
