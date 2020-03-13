import { Component } from '@angular/core';
import { ITransactionSummary } from '../../components/transaction-summary/transaction-summary.interface';
import { ScreenPart } from '../../decorators/screen-part.decorator';
import { ScreenPartComponent } from '../screen-part';
import { IActionItem } from '../../../core/actions/action-item.interface';


@ScreenPart({
  name: 'transactionHistory'
})
@Component({
  selector: 'app-transaction-history-part',
  templateUrl: './transaction-history-part.component.html',
  styleUrls: ['./transaction-history-part.component.scss']
})
export class TransactionHistoryPartComponent extends ScreenPartComponent<ITransactionSummary> {

  screenDataUpdated() {
  }

  onClick(actionItem: IActionItem): void {
    this.actionService.doAction(actionItem, this.screenData);
  }

}
