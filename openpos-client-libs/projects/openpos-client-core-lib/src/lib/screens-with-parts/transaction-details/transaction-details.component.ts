import { Component, Injector } from '@angular/core';
import { TransactionDetailsInterface } from './transaction-details.interface';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { OpenposMediaService, MediaBreakpoints } from '../../core/media/openpos-media.service';
import { Observable } from 'rxjs';

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

  constructor(injector: Injector, media: OpenposMediaService) {
    super(injector);
    this.isMobile = media.observe(new Map([
      [MediaBreakpoints.MOBILE_PORTRAIT, true],
      [MediaBreakpoints.MOBILE_LANDSCAPE, true],
      [MediaBreakpoints.TABLET_PORTRAIT, true],
      [MediaBreakpoints.TABLET_LANDSCAPE, false],
      [MediaBreakpoints.DESKTOP_PORTRAIT, false],
      [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    ]));
  }

  buildScreen() { }

}
