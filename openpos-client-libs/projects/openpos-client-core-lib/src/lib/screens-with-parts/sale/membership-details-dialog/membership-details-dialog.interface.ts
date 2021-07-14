import {IAbstractScreen} from '../../../core/interfaces/abstract-screen.interface';
import {CustomerDetails} from '../../../shared/screen-parts/customer-information/customer-information.interface';
import {SubscriptionAccount} from "../program-interface";

export interface MembershipDetailsDialogInterface extends IAbstractScreen {
    customer: CustomerDetails;
    membershipLabel: string;
    membershipCardIcon: string;
    profileIcon: string;
    subscriptionAccounts: SubscriptionAccount[];
}
