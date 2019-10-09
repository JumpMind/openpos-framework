
import { ITotal } from '../../core/interfaces/total.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { IActionItemGroup } from '../../core/interfaces/action-item-group.interface';

export interface SaleInterface extends IAbstractScreen {
    totals: ITotal[];
    transactionActive: boolean;
    customerName: string;
    noCustomerText: string;
    loyaltyButton: IActionItem;
    locationEnabled: boolean;
    locationOverridePrompt: string;
    checkoutButton: IActionItem;
    transactionMenuPrompt: string;
    transactionMenu: IActionItemGroup;
}
