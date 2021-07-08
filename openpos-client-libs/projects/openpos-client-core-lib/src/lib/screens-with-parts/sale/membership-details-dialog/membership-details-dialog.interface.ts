import {IAbstractScreen} from '../../../core/interfaces/abstract-screen.interface';
import {CustomerDetails} from '../../../shared/screen-parts/customer-information/customer-information.interface';
import {ActionItem} from "../../../core/actions/action-item";

export class EnrollmentItemProperty {
    name: string;
    value: string;
    type: string;
    icon: string;
}

export class EnrollmentItem {
    title: EnrollmentItemProperty;
    icon: string;
    details: EnrollmentItemProperty[];
    actionItemList: ActionItem[];
}

export class Plan {
    iconImageUrl: string;
    iconText: string;
    title: string;
    planCopy: string;
    signupActionItem: ActionItem;
}

export class SubscriptionAccounts {
    iconImageUrl: string;
    iconText: string;
    subscriptionAccountCopy: string;
    subscriptionAccountListItems: EnrollmentItem[];
    subscriptionAccountListTitle: string;
    plans: Plan[];
    signupActionItem: ActionItem;
}

export interface MembershipDetailsDialogInterface extends IAbstractScreen {
    customer: CustomerDetails;
    membershipLabel: string;
    membershipCardIcon: string;
    profileIcon: string;
    subscriptionAccounts: SubscriptionAccounts[];
}
