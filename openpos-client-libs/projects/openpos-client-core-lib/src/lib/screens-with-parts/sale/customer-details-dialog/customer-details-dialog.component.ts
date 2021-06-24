import {Component, Injector} from '@angular/core';
import {FormControl} from '@angular/forms';
import {CustomerDetailsDialogInterface, CustomerItemHistoryFilter} from './customer-details-dialog.interface';
import {DialogComponent} from '../../../shared/decorators/dialog-component.decorator';
import {PosScreen} from '../../pos-screen/pos-screen.component';
import {Observable} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';

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

  readonly itemsHistoryFilterController = new ItemsHistoryFilterController(this);

  constructor(
    injector: Injector, 
    private media: OpenposMediaService
  ) {
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

  buildScreen() {
    this.itemsHistoryFilterController.build();
  }

  getRewardsLabel() : string {
    return this.screen.rewardsLabel + ((this.screen.customer.rewards) ? ' (' + this.screen.customer.rewards.length + ')': '');
  }
}

class ItemsHistoryFilterController {
  fromDate = new FormControl();
  toDate = new FormControl();
  textFilter = new FormControl();

  private get serverState() {
    return this._parent.screen.itemHistoryFilter;
  }

  constructor(private readonly _parent: CustomerDetailsDialogComponent) {}

  fromDateFilterChanged(value: Date) {
    this.doItemHistoryFilterAction({ ...this.serverState, fromDate: value.toISOString() });
  }

  toDateFilterChanged(value: Date) {
    this.doItemHistoryFilterAction({ ...this.serverState, toDate: value.toISOString() });
  }
  
  onFilterKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      if (event.target instanceof HTMLInputElement) {
        event.target.blur();
        this.doItemHistoryFilterAction({...this.serverState, text: event.target.value});
      }
    }
  }

  build() {
    this.fromDate.setValue(this.serverState.fromDate);
    this.toDate.setValue(this.serverState.toDate);
    this.textFilter.setValue(this.serverState.text);
  }

  private doItemHistoryFilterAction(filter: CustomerItemHistoryFilter) {
    this._parent.doAction('ItemHistoryFilterChanged', filter);
  }
}
