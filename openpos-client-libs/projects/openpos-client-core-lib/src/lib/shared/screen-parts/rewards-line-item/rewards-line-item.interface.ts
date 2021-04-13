import {IActionItem} from '../../../core/actions/action-item.interface';

export interface Reward {
    promotionId: string;
    name: string;
    expirationDate: string;
    amount: number;
    applyButton: IActionItem;
};

export interface RewardsLineItemComponentInterface {
    expiresLabel: string;
    loyaltyIcon: string;
    expiredIcon: string;
    applyIcon: string;
}