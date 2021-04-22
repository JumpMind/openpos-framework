import { IAbstractScreen } from '../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import {Membership} from "../../shared/screen-parts/membership-display/memebership-display.interface";
import {IForm} from "../../core/interfaces/form.interface";
import {IFormElement} from "../../core/interfaces/form-field.interface";
import {Form, FormGroup} from "@angular/forms";
export interface LoyaltyCustomerFormInterface extends IAbstractScreen {
    form: IForm;
    formGroup: FormGroup;

    // IForm parts
    formElements: IFormElement[];
    requiresAtLeastOneValue: boolean;
    formErrors: string[];
    name: string;

    instructions: string;
    isStructuredForm: boolean;
    memberships: Membership[];
    membershipEnabled: boolean;
    membershipsLabel: string;
    noMembershipsLabel: string;

    submitButton: IActionItem;
    alternateSubmitActions: string[];
    imageUrl: string;
    profileIcon: string;
    locationIcon: string;
    loyaltyNumberIcon: string;
    phoneIcon: string;
    addPhone: IActionItem;
    removePhone: IActionItem;
    emailIcon: string;
    addEmail: IActionItem;
    removeEmail: IActionItem;
    countrySelected: IActionItem;
    stateSelected: IActionItem;
}
