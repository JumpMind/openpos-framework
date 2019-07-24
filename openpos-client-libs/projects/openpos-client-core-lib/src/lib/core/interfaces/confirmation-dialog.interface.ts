import { IActionItem } from '../actions/action-item.interface';
export interface IConfirmationDialog {
    title: string;
    message: string;
    confirmAction: IActionItem;
    cancelAction: IActionItem;
}
