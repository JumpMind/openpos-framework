
import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

export interface MobileSaleItemListInterface extends IAbstractScreen {
    items: ISellItem[];
    prompt: string;
    instructions: string;
    backgroundImage: string;
}
