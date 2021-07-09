import {ActionItem} from "../../core/actions/action-item";

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
    subscriptionAccountCopy: string;
    signupActionItem: ActionItem;
}

export class SubscriptionAccounts {
    iconImageUrl: string;
    iconText: string;
    subscriptionAccountCopy: string;
    subscriptionAccountListItems: EnrollmentItem[];
    subscriptionAccountListTitle: string;
    subscriptionPlans: Plan[];
    signupActionItem: ActionItem;
}