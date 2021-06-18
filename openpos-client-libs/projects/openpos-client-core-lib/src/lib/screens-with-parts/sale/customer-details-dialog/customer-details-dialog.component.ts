import {Component, Injector, OnDestroy} from '@angular/core';
import {CustomerDetailsDialogInterface} from './customer-details-dialog.interface';
import {DialogComponent} from '../../../shared/decorators/dialog-component.decorator';
import {PosScreen} from '../../pos-screen/pos-screen.component';
import {Observable} from 'rxjs';
import {MediaBreakpoints, OpenposMediaService} from '../../../core/media/openpos-media.service';
import {IActionItem} from "../../../core/actions/action-item.interface";
import {Configuration} from "../../../configuration/configuration";
import {ISelectableListData} from "../../../shared/components/selectable-item-list/selectable-list-data.interface";
import {SelectableItemListComponentConfiguration} from "../../../shared/components/selectable-item-list/selectable-item-list.component";
import {SelectionMode} from "../../../core/interfaces/selection-mode.enum";
import {Reward} from "../../../shared/screen-parts/rewards-line-item/rewards-line-item.interface";
import {KeyPressProvider} from "../../../shared/providers/keypress.provider";
import {Subscription} from "rxjs/internal/Subscription";
import {KeyboardClassKey} from "../../../keyboard/enums/keyboard-class-key.enum";
import {ActionService} from "../../../core/actions/action.service";

@DialogComponent({
  name: 'CustomerDetailsDialog'
})
@Component({
  selector: 'app-customer-details-dialog',
  templateUrl: './customer-details-dialog.component.html',
  styleUrls: ['./customer-details-dialog.component.scss']
})
export class CustomerDetailsDialogComponent extends PosScreen<CustomerDetailsDialogInterface> implements OnDestroy {

  isMobile: Observable<boolean>;
  spacebarSubscription: Subscription;
  constructor(public actionService: ActionService, injector: Injector, private media: OpenposMediaService, protected keyPresses: KeyPressProvider) {
    super(injector);
    this.initIsMobile();
    this.subscribeSpacebar();
  }

  public subscribeSpacebar() {
    this.spacebarSubscription = this.keyPresses.subscribe(KeyboardClassKey.Space, 1, (event: KeyboardEvent) => {
      if (event.repeat || event.type !== 'keydown' || !Configuration.enableKeybinds) { return; }
      if (event.type === 'keydown' && this.selectedReward) {
        if(this.selectedReward.actionButton && this.selectedReward.actionButton.enabled) {
          this.actionService.doAction(this.selectedReward.actionButton);
        }
      }
    })
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

  index = 0;
  selectedReward: Reward;
  public onItemChange(event: any): void {
    if(event == -1) {
      this.actionService.doAction(this.screen.backButton);
    } else {
      this.index = event;
      this.selectedReward = this.screen.customer.rewards[event];
    }
  }

  listData: Observable<ISelectableListData<Reward>>;
  listConfig: SelectableItemListComponentConfiguration;

  allRewards: Map<number, Reward> = new Map<number, Reward>();
  allDisabledRewards: Map<number, Reward> = new Map<number, Reward>();
  buildScreen() {
    for (let i = 0; i < this.screen.customer.rewards.length; i++) {
      const reward = this.screen.customer.rewards[i];
      reward.enabled = (reward.actionButton && reward.actionButton.enabled == true);
      this.allRewards.set(i, reward);
      if(!reward.enabled) {
        this.allDisabledRewards.set(i, reward);
      }
    }

      this.listData = new Observable<ISelectableListData<Reward>>((observer) => {
        observer.next({
          items: this.allRewards,
          disabledItems: this.allDisabledRewards
        } as ISelectableListData<Reward>);
      });

    this.listConfig = new SelectableItemListComponentConfiguration();
    if(this.allRewards.size === this.allDisabledRewards.size) {
      this.index = -1;
    } else {
      this.listConfig.defaultSelectItemIndex = this.index;
      this.selectedReward = this.screen.customer.rewards[this.index];
    }
    this.listConfig.selectionMode = SelectionMode.Single;
    this.listConfig.numItemsPerPage = Number.MAX_VALUE;
    this.listConfig.totalNumberOfItems = this.screen.customer.rewards.length;
  }

  getRewardsLabel() : string {
    return this.screen.rewardsLabel + ((this.screen.customer.rewards) ? ' (' + this.screen.customer.rewards.length + ')': '');
  }

  public keybindsEnabled(menuItem: IActionItem): boolean {
    return Configuration.enableKeybinds && !!menuItem.keybind && menuItem.keybind !== 'Enter';
  }

  ngOnDestroy() {
    if (this.spacebarSubscription) {
      this.spacebarSubscription.unsubscribe();
    }
  }
}
