import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { ITotal } from '../../../core/interfaces/total.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface ReturnTotalPanelInterface {
    items: ISellItem[];
    totals: ITotal[];
    grandTotal: ITotal;
    checkoutButton: IActionItem;
    loyaltyButton: IActionItem;
    receiptsButton: IActionItem;
    customer: { name: string, label: string, icon: string, id: string };
    readOnly: boolean;
}
