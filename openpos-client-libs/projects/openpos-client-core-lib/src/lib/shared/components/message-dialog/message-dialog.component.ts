import { Component, OnInit, Inject } from '@angular/core';
import { IMessageDialogProperties } from '../../../core/interfaces/message-dialog-properties.interface';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-message-dialog',
  templateUrl: './message-dialog.component.html',
  styleUrls: ['./message-dialog.component.scss']
})
export class MessageDialogComponent {

    constructor(@Inject(MAT_DIALOG_DATA) public properties: IMessageDialogProperties) {
    }
}
