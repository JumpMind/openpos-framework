import { IAbstractScreen } from '../../../core/interfaces/abstract-screen.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import {ICheckboxField} from "../../../core/interfaces/form-field.interface";

export interface ReturnTransDetailsInterface extends IAbstractScreen {
    items: ISellItem[];
    instructions: string;
    selectionButton: IActionItem;
    additionalButtons: IActionItem[];
    employeeTransaction: ICheckboxField;
}
