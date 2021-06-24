import {Membership} from '../membership-display/memebership-display.interface';
import {Reward} from '../rewards-line-item/rewards-line-item.interface';
import {RewardHistory} from '../rewards-history-line-item/rewards-history-line-item.interface';

export interface CustomerDetails {
    name: string,
    loyaltyNumber: string,
    phoneNumber: string,
    email: string,
    address: {
        line1: string,
        line2: string,
        city: string,
        state: string,
        postalCode: string
    },
    memberships: Membership[],
    rewards: Reward[],
    rewardHistory: RewardHistory[],
    birthDate: string
};

export interface CustomerInformationComponentInterface {
    emailIcon: string;
    phoneIcon: string;
    loyaltyNumberIcon: string;
    locationIcon: string;
    birthDateIcon: string;
}