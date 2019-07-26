import { Injectable } from '@angular/core';
import { IActionItem } from './action-item.interface';
import { Logger } from '../services/logger.service';
import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material';
import { QueueLoadingMessage } from '../services/session.service';
import { ActionMessage } from '../messages/action-message';
import { LoaderState } from '../../shared/components/loader/loader-state';
import { MessageProvider } from '../../shared/providers/message.provider';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { IUrlMenuItem } from './url-menu-item.interface';

@Injectable()
export class ActionService {

    private blockActions: boolean;
    private actionPayloads: Map<string, () => void> = new Map<string, () => void>();
    private actionDisablers = new Map<string, BehaviorSubject<boolean>>();

    constructor(
        private dialogService: MatDialog,
        private logger: Logger,
        private messageProvider: MessageProvider ) {
        messageProvider.getScopedMessages$().subscribe( message => {
            this.blockActions = false;
        });
    }

    async doAction( actionItem: IActionItem, payload?: any ) {
        const sendAction = await this.canPerformAction(actionItem);

        if ( sendAction ) {

            if (actionItem.hasOwnProperty('url')) {
                this.doUrlAction(actionItem as IUrlMenuItem);
                return;
            }

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

    private doUrlAction( urlItem: IUrlMenuItem ) {
        // check to see if we are an IURLMenuItem
        this.logger.info(`About to open: ${urlItem.url} in target mode: ${urlItem.targetMode}, with options: ${urlItem.options}`);
        window.open(urlItem.url, urlItem.targetMode, urlItem.options);
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

    public registerActionDisabler(action: string, disabler: Observable<boolean>): Subscription {
        if (!this.actionDisablers.has(action)) {
            this.actionDisablers.set(action, new BehaviorSubject<boolean>(false));
        }

        return disabler.subscribe(value => this.actionDisablers.get(action).next(value));
    }

    public actionIsDisabled$(action: string): Observable<boolean> {
        if (!this.actionDisablers.has(action)) {
            this.actionDisablers.set(action, new BehaviorSubject<boolean>(false));
        }

        return this.actionDisablers.get(action);
    }

    public actionIsDisabled(action: string): boolean {
        if (!this.actionDisablers.has(action)) {
            return false;
        }

        return this.actionDisablers.get(action).value;
    }

    private queueLoading() {
        this.messageProvider.sendMessage(new QueueLoadingMessage(LoaderState.LOADING_TITLE));
    }

    private async  canPerformAction( actionItem: IActionItem): Promise<boolean> {
        if ( actionItem.enabled === false) {
            this.logger.info('Not sending action because it was disabled');
            return false;
        }

        if ( this.blockActions ) {
            this.logger.info('Not sending action because previous action required a response that we are still waiting for');
            return false;
        }

        if ( this.actionIsDisabled(actionItem.action )) {
            this.logger.info('Not sending action because it was disabled by a disabler');
            return false;
        }

        if ( actionItem.confirmationDialog ) {
            this.logger.info('Confirming action');
            const dialogRef = this.dialogService.open(ConfirmationDialogComponent, { disableClose: true });
            dialogRef.componentInstance.confirmDialog = actionItem.confirmationDialog;
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
