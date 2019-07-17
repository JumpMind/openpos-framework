import { Injectable } from '@angular/core';
import { IActionItem } from '../interfaces/action-item.interface';
import { Logger } from './logger.service';
import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material';

@Injectable()
export class ActionService {
    constructor( private dialogService: MatDialog, private logger: Logger) {}

    async canPerformAction( action: IActionItem): Promise<boolean> {
        if ( !action.enabled ) {
            this.logger.info('Not sending action because it was disabled');
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

    doAction( action: IActionItem, payload?: any ) {

    }
}
