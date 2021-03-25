import {IAbstractScreen} from "../../core/interfaces/abstract-screen.interface";
import {IActionItem} from "../../core/actions/action-item.interface";
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";
import {SelectableItemInterface} from "../selection-list/selectable-item.interface";

export interface CustomerSearchResultDialogInterface extends IAbstractScreen {
    instructions: String;
    selectButton: IActionItem;
    viewButton: IActionItem;
    results: ICustomerDetails[]
}

export interface ICustomerDetails extends SelectableItemInterface{
    selected: boolean;
    enabled: boolean;
    name: string,
    loyaltyNumber: string,
    phoneNumber: string,
    email: string,
    address: {line1: string, line2: string, city: string, state: string, postalCode: string}
    memberships: Membership[]
}