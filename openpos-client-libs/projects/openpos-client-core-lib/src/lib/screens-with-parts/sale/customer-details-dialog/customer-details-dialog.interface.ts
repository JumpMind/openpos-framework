import {IAbstractScreen} from '../../../core/interfaces/abstract-screen.interface';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {CustomerDetails} from '../../../shared/screen-parts/customer-information/customer-information.interface';

export interface CustomerDetailsDialogInterface extends IAbstractScreen {
    message: string;
    customer: CustomerDetails;
    membershipEnabled: boolean;
    membershipPointsEnabled: boolean;
    membershipLabel: String;
    loyaltyPromotions: IActionItem;
    editButton: IActionItem;
    unlinkButton: IActionItem;
    doneButton: IActionItem;
    additionalActions: IActionItem[];
    contactLabel: string;
    rewardsLabel: string;
    rewardTabEnabled: boolean;
    rewardHistoryLabel: string;
    rewardHistoryTabEnabled: boolean;
    noPromotionsText: string;
    noMembershipsFoundLabel: string;

    profileIcon: string;
    membershipCardIcon: string;
}
