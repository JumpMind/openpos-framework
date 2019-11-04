import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ITotal } from '../../../core/interfaces/total.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';


export interface MobileTotalsPartInterface extends IAbstractScreen {
    totals: ITotal[];
    grandTotal: ITotal;
    checkoutButton: IActionItem;
    items: ISellItem[];
    readOnly: boolean;
}
