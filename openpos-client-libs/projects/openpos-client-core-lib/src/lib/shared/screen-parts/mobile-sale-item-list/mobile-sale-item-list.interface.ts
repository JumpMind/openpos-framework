
import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

export interface MobileSaleItemListInterface extends IAbstractScreen {
    prompt: string;
    providerKey: string;
    instructions: string;
    backgroundImage: string;
    enableCollapsibleItems: boolean;
}
