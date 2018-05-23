import { Component, OnInit } from '@angular/core';

import { SessionService } from '../../services/session.service';

@Component({
  selector: 'app-self-checkout-payment-status-component',
  templateUrl: './self-checkout-payment-status.component.html'
})
export class SelfCheckoutPaymentStatusComponent implements OnInit {

  screen: any;
  balanceDue = '0.00';
  instructions = '';
  additionalInstructions = '';

  constructor(public session: SessionService) { }

  show(screen: any) {
    this.screen = screen;

    this.balanceDue = this.screen.balanceDue;
    this.instructions = this.screen.instructions;
    if (this.screen.additionalInstructions) {
      this.additionalInstructions = this.screen.additionalInstructions;
    }

  }

  ngOnInit() {
  }
}