import {IActionItem} from "../../../core/actions/action-item.interface";

export interface Reward {
    promotionId: string;
    name: string;
    expirationDate: string;
    applyButton: IActionItem;
};

export interface RewardsLineItemComponentInterface {
    expiresLabel: string;
}