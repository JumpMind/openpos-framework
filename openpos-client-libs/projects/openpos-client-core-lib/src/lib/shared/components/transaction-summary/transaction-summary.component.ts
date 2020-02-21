import { Component, OnInit, Input } from '@angular/core';
import { ITransactionSummary } from './transaction-summary.interface';
import { IActionItem } from '../../../core/actions/action-item.interface';
import { ActionService } from '../../../core/actions/action.service';

@Component({
  selector: 'app-transaction-summary',
  templateUrl: './transaction-summary.component.html',
  styleUrls: ['./transaction-summary.component.scss']
})
export class TransactionSummaryComponent {

  @Input()
  transactionSummary: ITransactionSummary;

  @Input()
  border = false;

  constructor(private actionService: ActionService) { }

  onClick(actionItem: IActionItem): void {
    this.actionService.doAction(actionItem, this.transactionSummary);
  }

}
