import { Injectable } from '@angular/core';
import { IActionItem } from '../interfaces/action-item.interface';
import { Logger } from './logger.service';
import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material';
import { QueueLoadingMessage } from './session.service';
import { ActionMessage } from '../messages/action-message';
import { LoaderState } from '../../shared/components/loader/loader-state';
import { MessageProvider } from '../../shared/providers/message.provider';

@Injectable()
export class ActionService {

    private blockActions: boolean;
    private actionPayloads: Map<string, () => void> = new Map<string, () => void>();

    constructor( private dialogService: MatDialog, private logger: Logger, private messageProvider: MessageProvider) {
        messageProvider.getScopedMessages$().subscribe( message => {
            this.blockActions = false;
        });
    }

    async doAction( actionItem: IActionItem, payload?: any ) {
        const sendAction = await this.canPerformAction(actionItem);

        if ( sendAction ) {
            // First we will use the payload passed into this function then
            // Check if we have registered action payload
            if ( !payload && this.actionPayloads.has(actionItem.action)) {
                this.logger.info(`Checking registered action payload for ${actionItem.action}`);
                try {
                    payload = this.actionPayloads.get(actionItem.action)();
                } catch (e) {
                    throw new Error(`invalid action payload for ${actionItem.action}: ` + e);
                }
            }

            this.messageProvider.sendMessage( new ActionMessage(actionItem.action, payload));
            if ( !actionItem.doNotBlockForResponse ) {
                this.blockActions = true;
                this.queueLoading();
            }
        }
    }

    public registerActionPayload(actionName: string, actionValue: () => void) {
        this.actionPayloads.set(actionName, actionValue);
    }

    public unregisterActionPayloads() {
        this.actionPayloads.clear();
    }

    public unregisterActionPayload(actionName: string) {
        this.actionPayloads.delete(actionName);
    }

    private queueLoading() {
        this.messageProvider.sendMessage(new QueueLoadingMessage(LoaderState.LOADING_TITLE));
    }

    private async  canPerformAction( action: IActionItem): Promise<boolean> {
        if ( !action.enabled ) {
            this.logger.info('Not sending action because it was disabled');
            return false;
        }

        if ( this.blockActions ) {
            this.logger.info('Not sending action because previous action required a response that we are still waiting for');
            return false;
        }

        if ( action.confirmationDialog ) {
            this.logger.info('Confirming action');
            const dialogRef = this.dialogService.open(ConfirmationDialogComponent, { disableClose: true });
            dialogRef.componentInstance.confirmDialog = action.confirmationDialog;
            const result = await dialogRef.afterClosed().toPromise();

            // if we didn't confirm return and don't send the action to the server
            if (!result) {
                this.logger.info('Canceling action because confirmation was negative');
                return false;
            }
        }

        return true;
    }
}
