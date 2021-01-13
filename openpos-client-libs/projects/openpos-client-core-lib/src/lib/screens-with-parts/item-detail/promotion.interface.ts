import {IAbstractScreen} from '../../core/interfaces/abstract-screen.interface';

export interface IPromotionInterface extends IAbstractScreen {
    icon: string;
    promotionId: string;
    promotionName: string;
    priority: number;
    singleUse: boolean;
    autoApply: boolean;
    maxUses: number;
    vendorFunded: boolean;
    rewardApplicationTypeCode: string;
    forLoyaltyReward: boolean;
    promotionPrice: string;
}
