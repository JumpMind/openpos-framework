import {Component, HostListener} from '@angular/core';
import {IConfirmationDialog} from '../../actions/confirmation-dialog.interface';
import {MatDialogRef} from '@angular/material';

@Component({
    selector: 'app-confirmation-dialog',
    templateUrl: './confirmation-dialog.component.html',
    styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {

    public confirmDialog: IConfirmationDialog;

    constructor(public dialogRef: MatDialogRef<ConfirmationDialogComponent>) {
    }

    @HostListener('document:keydown.escape', ['$event']) onEscape(event: KeyboardEvent) {
        this.deny();
    }

    @HostListener('document:keydown.enter', ['$event']) onEnter(event: KeyboardEvent) {
        this.confirm();
    }

    confirm() {
        this.dialogRef.close(true);
    }

    deny() {
        this.dialogRef.close(false);
    }
}
