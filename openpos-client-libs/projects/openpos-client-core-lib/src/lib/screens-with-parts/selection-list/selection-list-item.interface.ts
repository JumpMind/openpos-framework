import { SelectionListItemDisplayProperty } from './selection-list-item-display-property.interface';

export interface SelectionListItem {
    title: string;
    properties: SelectionListItemDisplayProperty[];
    isSelected: boolean;
    enabled: boolean;
    itemImageUrl: string;
}
