import { IActionItem } from './action-item.interface';
export interface IConfirmationDialog {
    title: string;
    message: string;
    confirmAction: IActionItem;
    cancelAction: IActionItem;
}
