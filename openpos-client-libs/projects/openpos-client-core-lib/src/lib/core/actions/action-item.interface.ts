import {IConfirmationDialog} from './confirmation-dialog.interface';
import {IActionTimer} from './action-timer.interface';

export interface IActionItem {
    // Action to be sent to the server when the ActionItem is performed
    action: string;
    // If the UI element associated with this ActionItem should be enabled
    enabled?: boolean;
    // A display title to show on the UI element associated with this ActionItem
    title?: string;
    // The icon to display on the UI element associated with the ActionItem
    icon?: string;
    // If set the user will be presented with a dialog to confirm this action befor performing it
    confirmationDialog?: IConfirmationDialog;
    // If supported this would be a keybinding to use for activating this action
    keybind?: string;
    // If set this will be the display name to show for a given key. For example, Ctrl+F12 could be displayed as "Verify Price"
    keybindDisplayName?: string;
    // If set to true the client will not wait for a response from the server before allowing more actions
    doNotBlockForResponse?: boolean;
    // If set will display additional text before the title
    preTitleText?: string;
    // If set will display additional text after the title
    postTitleText?: string;
    /**
     * If set the action service will queue up the action if the action service is currently blocked
     */
    queueIfBlocked?: boolean;

    /**
     * If set, a timer will be associated with this action and this action will be
     * sent back to server when the timer triggers. Currently supported only on the GenericDialogComponent.
     */
    actionTimer?: IActionTimer;

    // TODO Remove this. This should not come from the server
    buttonSize?: string;
    // TODO Remove this. This should not come from the server
    fontSize?: string;
}
