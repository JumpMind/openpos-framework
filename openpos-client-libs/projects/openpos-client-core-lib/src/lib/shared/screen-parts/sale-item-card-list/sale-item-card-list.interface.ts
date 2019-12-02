import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

export interface SaleItemCardListInterface extends IAbstractScreen {
    providerKey: string;
    items: ISellItem[];
    readOnly: boolean;
    prompt: string;
    backgroundImage: string;
    enableCollapsibleItems: boolean;
}
