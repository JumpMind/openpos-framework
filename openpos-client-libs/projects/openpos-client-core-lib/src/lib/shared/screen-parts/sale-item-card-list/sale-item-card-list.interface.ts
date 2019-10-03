import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

export interface SaleItemCardListInterface extends IAbstractScreen {
    items: ISellItem[];
    readOnly: boolean;
    prompt: string;
    instructions: string;
    backgroundImage: string;
}
