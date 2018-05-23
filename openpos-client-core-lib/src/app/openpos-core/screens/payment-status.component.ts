import { IScreen } from '../common/iscreen';
import {Component, ViewChild, AfterViewInit, DoCheck} from '@angular/core';
import {SessionService} from '../services/session.service';
import { IMenuItem } from '../common/imenuitem';

@Component({
  selector: 'app-payment-status',
  templateUrl: './payment-status.component.html'
})
export class PaymentStatusComponent implements AfterViewInit, IScreen {

  screen: any;
  pinPadStatus = '0';

  constructor(public session: SessionService) {
  }

  show(screen: any) {
    this.screen = screen;
    this.pinPadStatus = this.screen.pinPadStatus;
  }


  ngAfterViewInit(): void {
    // Poll for status change if servers upports payment status updates.
    this.session.onAction('GetPaymentStatus');
  }

  public doMenuItemAction(menuItem: IMenuItem) {
    this.session.onAction(menuItem.action, null, menuItem.confirmationMessage );
  }

}
