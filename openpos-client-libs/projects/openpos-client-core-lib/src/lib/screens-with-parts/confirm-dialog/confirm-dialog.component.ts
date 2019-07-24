import { Component, OnInit, ViewChild } from '@angular/core';
import { SessionService } from '../../core/services/session.service';
import { IConfirmationDialog } from '../../core/actions/confirmation-dialog.interface';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { IScreen } from '../../shared/components/dynamic-screen/screen.interface';
import { ActionService } from '../../core/actions/action.service';

/**
 * @ignore
 */
@DialogComponent({
    name: 'ConfirmDialog'
})
@Component({
    selector: 'app-confirm-dialog',
    templateUrl: './confirm-dialog.component.html',
    styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent implements OnInit, IScreen {
    confirmDialog: IConfirmationDialog;

    constructor(public actionService: ActionService) {
    }

    ngOnInit() {
    }

    show(screen: any): void {
        this.confirmDialog = screen.confirmationDialog;
    }

    onCancel() {
        this.actionService.doAction(this.confirmDialog.cancelAction);
    }

    onConfirm() {
        this.actionService.doAction(this.confirmDialog.confirmAction);
    }
}
