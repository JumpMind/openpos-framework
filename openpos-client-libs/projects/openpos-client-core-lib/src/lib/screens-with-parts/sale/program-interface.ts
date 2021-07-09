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
    copy: string;
    signupActionItem: ActionItem;
}

export class SubscriptionAccounts {
    iconImageUrl: string;
    iconText: string;
    copy: string;
    enrollmentItems: EnrollmentItem[];
    listTitle: string;
    plans: Plan[];
    signupActionItem: ActionItem;
}