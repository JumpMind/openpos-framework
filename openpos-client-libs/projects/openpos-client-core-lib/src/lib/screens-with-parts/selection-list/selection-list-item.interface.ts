import { SelectionListItemDisplayProperty } from './selection-list-item-display-property.interface';
import {SelectableItemInterface} from './selectable-item.interface';


export class ISelectionListItem implements SelectableItemInterface {
    enabled: boolean;
    selected: boolean;
    title: string;
    properties: SelectionListItemDisplayProperty[];
    itemImageUrl: string;
}
