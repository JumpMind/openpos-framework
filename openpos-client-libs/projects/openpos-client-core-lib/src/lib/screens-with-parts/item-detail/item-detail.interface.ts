import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { DisplayProperty } from '../../shared/components/display-property/display-property.interface';

export interface ItemDetailInterface extends IAbstractScreen {
    imageUrls: string[];
    itemName: string;
    itemProperties: DisplayProperty[];
}
