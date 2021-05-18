import { IActionItem } from '../actions/action-item.interface';
import { IItem } from './item.interface';
import { IAdditionalLabel } from './additional-label.interface';

export interface ISellItem extends IItem {
    posItemId: string;
    quantity: number;
    imageUrl: string;
    productDescription: string;
    menuItems: IActionItem[];
    sellingPrice: string;
    color: string;
    size: string;
    longDescription: string;
    type: string;
    lineItemType: string;
    prop65Item: boolean;
    prop65WarningText: string;
    styleNumber: string;
    isGiftReceipt: boolean;
    originalAmount: string;
    discountAmount: string;
    labels: string[];
    icon: string;
    showSellingPrice: boolean;
    additionalLabels: IAdditionalLabel[];
    returnItemLabels: IAdditionalLabel[];
    orderItemLabels: IAdditionalLabel[];
    collapsedAdditionalLabels : IAdditionalLabel[];
    promoLabels: IAdditionalLabel[];
    optionsLabel: string;
    isOrderItem: boolean;
    isTender: boolean;
    svgImage: boolean;
    iconName:string;
}
