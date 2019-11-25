import { Component } from '@angular/core';
import { IConfirmationDialog } from '../../actions/confirmation-dialog.interface';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {

    public confirmDialog: IConfirmationDialog;

    constructor(public dialogRef: MatDialogRef<ConfirmationDialogComponent>) {
    }

    closeDialog(confirmed: boolean) {
      this.dialogRef.close(confirmed);
    }
}
