import { SessionService } from './../../core/services/session.service';

import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { ILine } from './line.interface';
import { PosScreen } from '../pos-screen/pos-screen.component';

/**
 * @ignore
 */
@Component({
  selector: 'app-dialog',
  templateUrl: './default-dialog.component.html',
  styleUrls: [ './default-dialog.component.scss']
})
export class DefaultDialogComponent extends PosScreen<any> {

  primaryButton: IActionItem;
  otherButtons: IActionItem[];
  messages: string[];
  lines: ILine[];

  constructor(public session: SessionService, public dialogRef: MatDialogRef<DefaultDialogComponent> ) {
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

  isCustomerFacing(): boolean {
    return this.screen.customerFacing && ['CUSTOMER_CONFIRMATION'].indexOf(this.screen.subType) >= 0;
  }
}
