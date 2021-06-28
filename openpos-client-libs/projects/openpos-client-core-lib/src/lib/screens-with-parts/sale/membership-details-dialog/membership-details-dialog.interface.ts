import {IAbstractScreen} from '../../../core/interfaces/abstract-screen.interface';
import {CustomerDetails} from '../../../shared/screen-parts/customer-information/customer-information.interface';

export interface MembershipDetailsDialogInterface extends IAbstractScreen {
    customer: CustomerDetails;
    membershipLabel: String;
    membershipCardIcon: string;
    profileIcon: string;
}
