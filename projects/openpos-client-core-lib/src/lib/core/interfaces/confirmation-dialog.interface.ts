import { IActionItem } from '../interfaces/menu-item.interface';
export interface IConfirmationDialog {
    title: string;
    message: string;
    /** @deprecated Use confirmAction instead */
    confirmButtonName: string;
    confirmAction: IActionItem;
    /** @deprecated Use cancelAction instead */
    cancelButtonName: string;
    cancelAction: IActionItem;
}
