import { ITotal } from '../../../core/interfaces/total.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface ITender extends ITotal {
    cardNumber: string;
    voidButton: IActionItem;
}
