import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { ITotal } from '../../../core/interfaces/total.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';

export interface SaleTotalPanelInterface extends IAbstractScreen {
    totals: ITotal[];
    grandTotal: ITotal;
    itemCount: ITotal;
    checkoutButton: IActionItem;
    helpButton: IActionItem;
    logoutButton: IActionItem;
    loyaltyButton: IActionItem;
    promoButton: IActionItem;
    customer: { name: string, label: string, icon: string, id: string };
    taxExemptCertificateDetail: {label: string, value: string};
    readOnly: boolean;
    prompt: string;
}
