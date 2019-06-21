import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, OnInit } from '@angular/core';
import { SellScreenUtils } from '../sell-template/sell/sell-screen.interface';
import { ScreenComponent } from '../../../shared/decorators/screen-component.decorator';
import { AbstractTemplate } from '../../../core/components/abstract-template';
import { StatusBarData } from '../../../shared/components/status-bar/status-bar-data';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'BlankWithBar'
})
@Component({
  selector: 'app-blank-with-bar',
  templateUrl: './blank-with-bar.component.html',
  styleUrls: ['./blank-with-bar.component.scss']
})
export class BlankWithBarComponent extends AbstractTemplate<any> implements OnInit {

  template: any;
  statusBar: StatusBarData;

  constructor(public overlayContainer: OverlayContainer) {
      super();
  }

  ngOnInit() {
  }

  show(template: any) {
    this.template = template;
    this.statusBar = SellScreenUtils.getStatusBar(template);
  }

  buildTemplate() {}

}
