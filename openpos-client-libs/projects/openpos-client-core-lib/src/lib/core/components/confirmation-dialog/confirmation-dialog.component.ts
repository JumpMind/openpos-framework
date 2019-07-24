import { Component } from '@angular/core';
import { IConfirmationDialog } from '../../actions/confirmation-dialog.interface';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {

    public confirmDialog: IConfirmationDialog;
}
