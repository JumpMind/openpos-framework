import { Component, ViewChild, ElementRef } from '@angular/core';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';
import { Observable } from 'rxjs';
import { ISelectableListData } from '../../shared/components/selectable-item-list/selectable-list-data.interface';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'ItemSearchResults'
})
@Component({
  selector: 'app-item-search-results',
  templateUrl: './item-search-results.component.html',
  styleUrls: ['./item-search-results.component.scss']
})
export class ItemSearchResultsComponent extends PosScreen<any> {

  @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;

  listConfig = new SelectableItemListComponentConfiguration();
  listData: Observable<ISelectableListData<any>>;
  index = -1;

  constructor() {
    super();
  }

  buildScreen() {
    const allItems = new Map<number, any>();
    const allDisabledItems = new Map<number, any>();
    for (let i = 0; i < this.screen.items.length; i++) {
        const item = this.screen.items[i];
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
    this.listConfig.totalNumberOfItems = this.screen.items.length;
    this.listConfig.defaultSelectItemIndex = 0;

    this.scrollToTop();
  }

  public onItemListChange(event: any[]): void {
  }

  public onItemChange(event: any): void {
    this.index = event;
  }

  public doMenuItemAction(menuItem: IActionItem) {
    this.session.onAction(menuItem, this.index);
  }

  scrollToTop(): void {
    try {
      this.scrollList.nativeElement.scrollTop = 0;
    } catch (err) { }
  }

  public selectDisabled(): boolean {
    return !this.screen.actionButton.enabled || this.index < 0;
  }

}
