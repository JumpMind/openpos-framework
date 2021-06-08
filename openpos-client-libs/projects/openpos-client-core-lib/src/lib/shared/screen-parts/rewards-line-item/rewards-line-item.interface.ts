import {IActionItem} from '../../../core/actions/action-item.interface';

export interface Reward {
    promotionId: string;
    name: string;
    expirationDate: string;
    amount: number;
    applyButton: IActionItem;
    otherStatusIcon: string;
    barcode: string;

    selected: boolean;
    enabled: boolean;
};

export interface RewardsLineItemComponentInterface {
    appliedLabel: string;
    expiresLabel: string;
    loyaltyIcon: string;
    expiredIcon: string;
    applyIcon: string;
    appliedIcon: string;
}