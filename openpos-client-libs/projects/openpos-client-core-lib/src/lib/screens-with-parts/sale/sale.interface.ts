
import { ITotal } from '../../core/interfaces/total.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { IOrderSummary } from '../../core/interfaces/order-summary.interface';

export interface SaleInterface extends IAbstractScreen {
    totals: ITotal[];
    transactionActive: boolean;
    customerName: string;
    noCustomerText: string;
    loyaltyButton: IActionItem;
    locationEnabled: boolean;
    locationOverridePrompt: string;
    checkoutButton: IActionItem;
    providerKey: string;
    orders: IOrderSummary[];
}
