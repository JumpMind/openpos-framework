import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";
import {SelectableItemInterface} from "../selection-list/selectable-item.interface";


export interface ICustomerDetails extends SelectableItemInterface{
    name: string,
    loyaltyNumber: string,
    phoneNumber: string,
    email: string,
    address: {line1: string, line2: string, city: string, state: string, postalCode: string}
    memberships: Membership[],
    privacyRestrictedMessage: string
}