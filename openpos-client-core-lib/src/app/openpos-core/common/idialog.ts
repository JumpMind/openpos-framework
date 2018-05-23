import { IMenuItem } from './imenuitem';
import { MatDialogConfig } from '@angular/material';

export interface IDialog {
    title: string;
    buttons: IMenuItem[];
    subType: string;
    message: string[];
}

export interface OpenPOSDialogConfig extends MatDialogConfig {
    /** If true, will cause the dialog action chosen by the user to be invoked before
     * the dialog is closed.  Default behavior is to invoke the action after closing.
     * This is needed in order to allow for interception of the action and to support
     * modification/addition of a payload back to the server. */
    executeActionBeforeClose?: boolean;
}
