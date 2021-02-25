import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
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

  get transactionDate(): Date {
    // safari will throw an invalid date error if the date 
    // and time isn't seperatd by a T. The data comes in 
    // with the separation of a space.
    return new Date(this.transactionSummary.transactionDate.replace(' ', 'T'));
  };

  constructor(private actionService: ActionService, private transactionService: TransactionService) { }

  onClick(actionItem: IActionItem): void {
    this.actionService.doAction(actionItem, this.transactionSummary);
  }

  ngOnChanges(changes: SimpleChanges) {
    this.statusClass = this.transactionService.mapStatusToCssClass(this.transactionSummary.status);
  }
}
