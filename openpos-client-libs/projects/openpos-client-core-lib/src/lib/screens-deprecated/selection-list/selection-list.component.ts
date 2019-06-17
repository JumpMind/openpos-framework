import { Component } from '@angular/core';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';
import { ISelectableListData } from '../../shared/components/selectable-item-list/selectable-list-data.interface';
import { Observable } from 'rxjs';

/**
 * @ignore
 */
@Component({
  selector: 'app-selection-list',
  templateUrl: './selection-list.component.html',
  styleUrls: ['./selection-list.component.scss']
})
export class SelectionListComponent extends PosScreen<any> {
  listData: Observable<ISelectableListData<any>>;
  listConfig = new SelectableItemListComponentConfiguration();
  index = -1;
  lastSelection = -1;

  constructor() {
    super();
  }

  buildScreen() {
    if (this.screen.template.localMenuItems) {
      let i: number;
      for (i = 0; i < this.screen.template.localMenuItems.length; i++) {
        this.session.registerActionPayload(this.screen.template.localMenuItems[i].action, () => {
          return this.index;
        });
      }
    }

    const allItems = new Map<number, any>();
    const allDisabledItems = new Map<number, any>();
    for (let i = 0; i < this.screen.selectionList.length; i++) {
      const item = this.screen.selectionList[i];
      allItems.set(i, item);
      if (!item.enabled) {
        allDisabledItems.set(i, item);
      }
    }
    this.listData = new Observable<ISelectableListData<any>>((observer) => {
      observer.next({
          items: allItems,
          disabledItems: allDisabledItems,
      } as ISelectableListData<any>);
    });

    this.listConfig = new SelectableItemListComponentConfiguration();
    this.listConfig.selectionMode = this.screen.multiSelect ? SelectionMode.Multiple : SelectionMode.Single;
    this.listConfig.numItemsPerPage = Number.MAX_VALUE;
    this.listConfig.totalNumberOfItems = this.screen.selectionList.length;
    if (this.screen.defaultSelectItemIndex !== null && this.screen.defaultSelectItemIndex !== undefined) {
      this.listConfig.defaultSelectItemIndex = this.screen.defaultSelectItemIndex;
    }
  }

  public onItemListChange(event: any[]): void {
    // this.selectedItems = event;
    // this.session.onAction("SelectedItemsChanged", this.selectedItems);
  }

  public onItemChange(event: any): void {
    this.index = event;
    if (this.screen.selectionChangedAction && this.index !== this.lastSelection) {
      this.lastSelection = this.index;
      this.session.onAction(this.screen.selectionChangedAction, this.index);
    }
  }

  public doMenuItemAction(menuItem: IActionItem) {
    this.session.onAction(menuItem, this.index);
  }

}
