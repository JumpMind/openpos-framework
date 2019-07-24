import { DisplayProperty } from '../../../shared/components/display-property/display-property.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';


export interface SaleFooterInterface {
    itemCount: string;
    grandTotal: DisplayProperty;
    checkoutButton: IActionItem;
}
