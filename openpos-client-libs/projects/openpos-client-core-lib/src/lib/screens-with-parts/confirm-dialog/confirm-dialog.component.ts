import { Component, OnInit, ViewChild } from '@angular/core';
import { SessionService } from '../../core/services/session.service';
import { IConfirmationDialog } from '../../core/interfaces/confirmation-dialog.interface';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';
import { IScreen } from '../../core/components/dynamic-screen/screen.interface';

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

    constructor(public session: SessionService) {
    }

    ngOnInit() {
    }

    show(screen: any): void {
        this.confirmDialog = screen.confirmationDialog;
    }

    onCancel() {
        this.session.onAction(this.confirmDialog.cancelAction);
    }

    onConfirm() {
        this.session.onAction(this.confirmDialog.confirmAction);
    }
}
