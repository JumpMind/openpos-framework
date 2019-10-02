import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ITotal } from '../../../core/interfaces/total.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';

export interface SaleTotalPanelInterface extends IAbstractScreen {
    items: ISellItem[];
    totals: ITotal[];
    grandTotal: ITotal;
    checkoutButton: IActionItem;
    logoutButton: IActionItem;
    loyaltyButton: IActionItem;
    promoButton: IActionItem;
    customer: { name: string, label: string, icon: string, id: string };
    readOnly: boolean;
}
