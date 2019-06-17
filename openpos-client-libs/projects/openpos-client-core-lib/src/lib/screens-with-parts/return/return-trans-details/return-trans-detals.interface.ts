import { IAbstractScreen } from './../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

export interface ReturnTransDetailsInterface extends IAbstractScreen {
    items: ISellItem[];
    instructions: string;
    selectionButton: IActionItem;
}
