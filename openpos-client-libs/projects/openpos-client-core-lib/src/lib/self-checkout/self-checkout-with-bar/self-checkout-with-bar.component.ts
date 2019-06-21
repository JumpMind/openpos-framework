
import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, OnInit } from '@angular/core';
import { SelfCheckoutStatusBarData } from '../self-checkout-statusbar/self-checkout-status-bar-data';
import { AbstractTemplate } from '../../core/components/abstract-template';
import { ISellTemplate } from '../../screens-deprecated/templates/sell-template/sell/sell-template.interface';
import { SellScreenUtils } from '../../screens-deprecated/templates/sell-template/sell/sell-screen.interface';

@Component({
  selector: 'app-selfcheckout-with-bar',
  templateUrl: './self-checkout-with-bar.component.html',
  styleUrls: ['./self-checkout-with-bar.component.scss']
})
export class SelfCheckoutWithBarComponent extends AbstractTemplate<any> implements OnInit {

  template: ISellTemplate;
  statusBar: SelfCheckoutStatusBarData;

  constructor(public overlayContainer: OverlayContainer) {
      super();
  }

  ngOnInit() {
  }

  show(screen: any) {
    this.template = screen.template;
    this.statusBar = SellScreenUtils.getSelfCheckoutStatusBar( screen );
  }

  buildTemplate() {}

}

