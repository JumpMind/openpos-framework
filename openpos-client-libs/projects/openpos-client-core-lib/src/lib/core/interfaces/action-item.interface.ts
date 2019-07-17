import { IConfirmationDialog } from './confirmation-dialog.interface';

export interface IActionItem {
    enabled?: boolean;
    action: string;
    title?: string;
    icon?: string;
    confirmationDialog?: IConfirmationDialog;
    // TODO Remove this. This should not come from the server
    buttonSize?: string;
    // TODO Remove this. This should not come from the server
    fontSize?: string;
    keybind?: string;
    doNotBlockForResponse?: boolean;
}
