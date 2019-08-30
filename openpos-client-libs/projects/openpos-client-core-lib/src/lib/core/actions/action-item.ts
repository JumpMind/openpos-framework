import { IActionItem } from './action-item.interface';
import { IConfirmationDialog } from './confirmation-dialog.interface';

export class ActionItem implements IActionItem {
    enabled?: boolean;
    title?: string;
    icon?: string;
    confirmationDialog?: IConfirmationDialog;
    keybind?: string;
    doNotBlockForResponse?: boolean;
    buttonSize?: string;
    fontSize?: string;

    constructor(public action: string) {}
}