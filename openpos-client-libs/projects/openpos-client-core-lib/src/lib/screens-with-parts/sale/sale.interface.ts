
import { ITotal } from '../../core/interfaces/total.interface';
import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { IOrderSummary } from '../../core/interfaces/order-summary.interface';

export interface SaleInterface extends IAbstractScreen {
    totals: ITotal[];
    logoutButton: IActionItem;
    transactionActive: boolean;
    customerName: string;
    customer: { name: string, label: string, icon: string, id: string };
    noCustomerText: string;
    linkedCustomerButton: IActionItem;
    locationEnabled: boolean;
    locationOverridePrompt: string;
    checkoutButton: IActionItem;
    providerKey: string;
    orders: IOrderSummary[];
    removeOrderAction: IActionItem;
    readOnly: boolean;
    scanIcon: string;
}
