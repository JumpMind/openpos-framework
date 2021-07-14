import {IAbstractScreen} from '../../../core/interfaces/abstract-screen.interface';
import {EnrollmentItem, Plan} from "../program-interface";
import {ActionItem} from "../../../core/actions/action-item";

export interface MembershipPlanDetailsDialogInterface extends IAbstractScreen {
    enrollmentItem: EnrollmentItem;
    plan: Plan;
    doneButton: ActionItem;
}
