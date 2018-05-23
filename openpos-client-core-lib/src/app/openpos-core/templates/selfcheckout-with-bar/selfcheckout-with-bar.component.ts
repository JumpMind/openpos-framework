import { IScan, ISellTemplate } from './../sell/isell-template';
import { over } from '@stomp/stompjs';
import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, OnInit } from '@angular/core';
import { IScreen } from '../../common/iscreen';
import { SessionService } from '../../services/session.service';
import { AbstractTemplate } from '../../common/abstract-template';
import { SelfCheckoutStatusBarData } from '../../common/screen-interfaces/selfCheckoutStatusBarData';
import { SellScreenUtils } from '../sell/iSellScreen';

@Component({
  selector: 'app-selfcheckout-with-bar',
  templateUrl: './selfcheckout-with-bar.component.html',
  styleUrls: ['./selfcheckout-with-bar.component.scss']
})
export class SelfCheckoutWithBarComponent extends AbstractTemplate implements OnInit {

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

}

