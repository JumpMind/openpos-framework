import { Component, Input, HostListener, Optional, Output, EventEmitter } from '@angular/core';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';
import { ITransactionReceipt } from './transaction-receipt.interface';

@Component({
  selector: 'app-receipt-card',
  templateUrl: './receipt-card.component.html',
  styleUrls: ['./receipt-card.component.scss']
})
export class ReceiptCardComponent {

  @Input()
  public receipt: ITransactionReceipt;

  @Input()
  public removeReceiptAction: IActionItem;

  @Output()
  removeButtonClick = new EventEmitter();

  public hover = false;

  constructor(@Optional() public actionService: ActionService) {
  }

  @HostListener('mouseenter')
  onMouseEnter() {
    this.hover = true;
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    this.hover = false;
  }

  onRemoveAction() {
    if (this.actionService) {
      this.actionService.doAction(this.removeReceiptAction, this.receipt.transactionNumber);
    } else {
      this.removeButtonClick.emit(this.receipt);
    }
  }

}
