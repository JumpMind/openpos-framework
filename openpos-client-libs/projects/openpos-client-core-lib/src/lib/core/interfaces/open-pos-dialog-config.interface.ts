import { MatDialogConfig } from '@angular/material';

export interface OpenPOSDialogConfig extends MatDialogConfig {
    /** If true, will cause the dialog action chosen by the user to be invoked before
     * the dialog is closed.  Default behavior is to invoke the action after closing.
     * This is needed in order to allow for interception of the action and to support
     * modification/addition of a payload back to the server. */
    executeActionBeforeClose?: boolean;
    closeable?: boolean;

}
