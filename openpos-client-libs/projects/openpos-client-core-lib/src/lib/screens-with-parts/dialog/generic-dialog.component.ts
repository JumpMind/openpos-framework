
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { PosScreen } from '../pos-screen.component';
import { ILine } from '../../screens-deprecated/dialog/line.interface';
import { Component } from '@angular/core';
import { SessionService } from '../../core/services/session.service';
import { MatDialogRef } from '@angular/material';
import { DialogInterface } from './dialog.interface';
import { DialogComponent } from '../../shared/decorators/dialog-component.decorator';

@DialogComponent({
    name: 'Dialog',
})
@Component({
  selector: 'app-dialog',
  templateUrl: './generic-dialog.component.html',
  styleUrls: [ './generic-dialog.component.scss']
})
export class GenericDialogComponent extends PosScreen<DialogInterface> {

  primaryButton: IActionItem;
  otherButtons: IActionItem[];
  messages: string[];
  lines: ILine[];

  constructor(public session: SessionService, public dialogRef: MatDialogRef<GenericDialogComponent> ) {
      super();
  }

  buildScreen(): void {
    if (this.screen.message && this.screen.message.length === 1) {
        this.messages = this.screen.message[0].split('\n');
    } else {
        this.messages = this.screen.message;
    }

    this.lines = this.screen.messageLines;

    if ( this.screen.buttons ) {
      this.primaryButton = this.screen.buttons[0];
    }

    if ( this.screen.buttons && this.screen.buttons.length > 1 ) {
      this.otherButtons = this.screen.buttons.slice(1, this.screen.buttons.length);
    }
  }
}
