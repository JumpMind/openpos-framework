import {Component, Injector} from '@angular/core';
import {CustomerDetailsDialogInterface} from './customer-details-dialog.interface';
import {DialogComponent} from '../../../shared/decorators/dialog-component.decorator';
import {PosScreen} from '../../pos-screen/pos-screen.component';
import {Observable} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {IActionItem} from "../../../core/actions/action-item.interface";
import {Configuration} from "../../../configuration/configuration";

@DialogComponent({
  name: 'CustomerDetailsDialog'
})
@Component({
  selector: 'app-customer-details-dialog',
  templateUrl: './customer-details-dialog.component.html',
  styleUrls: ['./customer-details-dialog.component.scss']
})
export class CustomerDetailsDialogComponent extends PosScreen<CustomerDetailsDialogInterface> {

  isMobile: Observable<boolean>;
  constructor(injector: Injector, private media: OpenposMediaService) {
    super(injector);
    this.initIsMobile();
  }

  initIsMobile(): void {
    this.isMobile = this.media.observe(new Map([
      [MediaBreakpoints.MOBILE_PORTRAIT, true],
      [MediaBreakpoints.MOBILE_LANDSCAPE, true],
      [MediaBreakpoints.TABLET_PORTRAIT, true],
      [MediaBreakpoints.TABLET_LANDSCAPE, true],
      [MediaBreakpoints.DESKTOP_PORTRAIT, false],
      [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
    ]));
  }

  buildScreen() {}

  getRewardsLabel() : string {
    return this.screen.rewardsLabel + ((this.screen.customer.rewards) ? ' (' + this.screen.customer.rewards.length + ')': '');
  }

  public keybindsEnabled(menuItem: IActionItem): boolean {
    return Configuration.enableKeybinds && !!menuItem.keybind && menuItem.keybind !== 'Enter';
  }
}
