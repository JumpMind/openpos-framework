export interface RewardHistory {
    promotionId: string;
    name: string;
    expirationDate: string;
    amount: number;
    redeemed: boolean;
};

export interface RewardsHistoryLineItemComponentInterface {
    redeemedLabel: string;
    expiredLabel: string;
    loyaltyIcon: string;
    expiredIcon: string;
}