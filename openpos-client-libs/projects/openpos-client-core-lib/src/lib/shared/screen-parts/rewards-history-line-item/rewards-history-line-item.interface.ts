export interface RewardHistory {
    promotionId: string;
    name: string;
    expirationDate: string;
    redeemed: boolean;
};

export interface RewardsHistoryLineItemComponentInterface {
    redeemedLabel: string;
    expiredLabel: string;
    loyaltyIcon: string;
}