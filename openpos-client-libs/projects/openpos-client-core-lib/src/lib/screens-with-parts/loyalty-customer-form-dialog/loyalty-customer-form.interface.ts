import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";
export interface LoyaltyCustomerFormInterface extends IAbstractScreen {
    instructions: string;
    submitButton: IActionItem;
    alternateSubmitActions: string[];
    imageUrl: string;
    profileIcon: string;
    locationIcon: string;
    loyaltyNumberIcon: string;
    phoneIcon: string;
    addPhone: IActionItem;
    removePhone: IActionItem;
    clearPhone: IActionItem;
    emailIcon: string;
    addEmail: IActionItem;
    removeEmail: IActionItem;
    clearEmail: IActionItem;
    countrySelected: IActionItem;
    stateSelected: IActionItem;
    memberships: Membership[];
    membershipEnabled: boolean;
    membershipsLabel: string;
    noMembershipsLabel: string;
}
