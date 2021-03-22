import { Component, Injector } from '@angular/core';
import { TransactionDetailsInterface } from './transaction-details.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { OpenposMediaService, MediaBreakpoints } from '../../core/media/openpos-media.service';
import { Observable } from 'rxjs';
import { SessionService } from '../../core/services/session.service';
import { PrinterService } from '../../core/platform-plugins/printers/printer.service';
import { MessageTypes } from '../../core/messages/message-types';

@ScreenComponent({
  name: 'TransactionDetails'
})
@Component({
  selector: 'app-transaction-details',
  templateUrl: './transaction-details.component.html',
  styleUrls: ['./transaction-details.component.scss']
})
export class TransactionDetailsComponent extends PosScreen<TransactionDetailsInterface> {

  isMobile: Observable<boolean>;

  constructor(
    injector: Injector, media: OpenposMediaService,
    private sessionService: SessionService,
    private printerService: PrinterService
  ) {
    super(injector);
    this.isMobile = media.observe(new Map([
      [MediaBreakpoints.MOBILE_PORTRAIT, true],
      [MediaBreakpoints.MOBILE_LANDSCAPE, true],
      [MediaBreakpoints.TABLET_PORTRAIT, true],
      [MediaBreakpoints.TABLET_LANDSCAPE, false],
      [MediaBreakpoints.DESKTOP_PORTRAIT, false],
      [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    ]));
    this.sessionService.getMessages(MessageTypes.PRINT).subscribe((message) => {
      this.printReceipt(message);
    });
  }

  buildScreen() { }

  printReceipt(message): void {
    this.printerService.print("BROWSER", message.html);
  }
}
