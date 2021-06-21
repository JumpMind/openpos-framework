import {IActionItem} from '../../../core/actions/action-item.interface';

export interface Reward {
    promotionId: string;
    name: string;
    expirationDate: string;
    amount: number;
    actionButton: IActionItem;
    actionIcon: string;
    statusText: string;
    barcode: string;

    selected: boolean;
    enabled: boolean;
};

export interface RewardsLineItemComponentInterface {
    expiresLabel: string;
    loyaltyIcon: string;
    expiredIcon: string;
}