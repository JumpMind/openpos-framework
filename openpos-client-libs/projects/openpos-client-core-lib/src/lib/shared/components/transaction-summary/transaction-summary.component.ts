import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ITransactionSummary} from './transaction-summary.interface';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {ActionService} from '../../../core/actions/action.service';
import {TransactionService} from '../../../core/services/transaction.service';
import {TransTypeEnum} from '../../trans-type.enum';

@Component({
  selector: 'app-transaction-summary',
  templateUrl: './transaction-summary.component.html',
  styleUrls: ['./transaction-summary.component.scss']
})
export class TransactionSummaryComponent implements OnChanges {
  @Input()
  transactionSummary: ITransactionSummary;

  statusClass: string;
  TransTypeEnum = TransTypeEnum;

  constructor(private actionService: ActionService, private transactionService: TransactionService) { }

  onClick(actionItem: IActionItem): void {
    this.actionService.doAction(actionItem, this.transactionSummary);
  }

  ngOnChanges(changes: SimpleChanges) {
    this.statusClass = this.transactionService.mapStatusToCssClass(this.transactionSummary.status);
  }
}
