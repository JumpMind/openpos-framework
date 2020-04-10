import {Component} from '@angular/core';
import {ITransactionSummary} from '../../components/transaction-summary/transaction-summary.interface';
import {ScreenPart} from '../../decorators/screen-part.decorator';
import {ScreenPartComponent} from '../screen-part';
import {IActionItem} from '../../../core/actions/action-item.interface';
import {MediaBreakpoints} from '../../../core/media/openpos-media.service';


@ScreenPart({
  name: 'transactionHistory'
})
@Component({
  selector: 'app-transaction-history-part',
  templateUrl: './transaction-history-part.component.html',
  styleUrls: ['./transaction-history-part.component.scss']
})
export class TransactionHistoryPartComponent extends ScreenPartComponent<ITransactionSummary> {
  ngOnInit(): void {
    super.ngOnInit();

    this.isMobile$ = this.mediaService.observe(new Map([
      [MediaBreakpoints.MOBILE_PORTRAIT, true],
      [MediaBreakpoints.MOBILE_LANDSCAPE, true],
      [MediaBreakpoints.TABLET_PORTRAIT, true],
      [MediaBreakpoints.TABLET_LANDSCAPE, false],
      [MediaBreakpoints.SMALL_DESKTOP_PORTRAIT, true],
      [MediaBreakpoints.SMALL_DESKTOP_LANDSCAPE, false],
      [MediaBreakpoints.TABLET_LANDSCAPE, false],
      [MediaBreakpoints.DESKTOP_PORTRAIT, false],
      [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    ]));
  }

  screenDataUpdated() {
  }

  onClick(actionItem: IActionItem): void {
    this.actionService.doAction(actionItem, this.screenData);
  }

}
