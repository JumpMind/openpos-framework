import { Component, Input, HostListener } from '@angular/core';
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

  public hover = false;

  constructor(public actionService: ActionService) {
  }

  @HostListener('mouseenter')
  onMouseEnter() {
    this.hover = true;
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    this.hover = false;
  }

}
