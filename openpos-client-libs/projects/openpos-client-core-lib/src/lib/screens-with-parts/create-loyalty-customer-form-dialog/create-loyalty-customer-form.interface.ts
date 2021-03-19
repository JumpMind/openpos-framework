import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";
export interface CreateLoyaltyCustomerFormInterface extends IAbstractScreen {
    instructions: string;
    submitButton: IActionItem;
    alternateSubmitActions: string[];
    imageUrl: string;
    addPhone: IActionItem;
    removePhone: IActionItem;
    addEmail: IActionItem;
    removeEmail: IActionItem;
    memberships: Membership[];
    membershipsLabel: string;
    noMembershipsLabel: string;
}
