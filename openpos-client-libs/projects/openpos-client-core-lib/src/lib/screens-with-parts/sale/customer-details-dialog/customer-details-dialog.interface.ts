import {IAbstractScreen} from '../../../core/interfaces/abstract-screen.interface';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {CustomerDetails} from '../../../shared/screen-parts/customer-information/customer-information.interface';

export interface CustomerDetailsDialogInterface extends IAbstractScreen {
    message: string;
    customer: CustomerDetails;
    membershipEnabled: boolean;
    membershipLabel: String;
    loyaltyPromotions: IActionItem;
    editButton: IActionItem;
    unlinkButton: IActionItem;
    doneButton: IActionItem;
    contactLabel: string;
    rewardsLabel: string;
    rewardHistoryLabel: string;
    noPromotionsText: string;
    noMembershipsFoundLabel: string;

    profileIcon: string;
    membershipCardIcon: string;
}
