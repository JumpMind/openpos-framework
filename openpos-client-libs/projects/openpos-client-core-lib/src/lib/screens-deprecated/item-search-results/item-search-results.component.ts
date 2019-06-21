import { Component, ViewChild, ElementRef } from '@angular/core';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';

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

  listConfig = new SelectableItemListComponentConfiguration<any>();
  index = -1;

  constructor() {
    super();
  }

  buildScreen() {
    this.listConfig = new SelectableItemListComponentConfiguration<any>();
    if (this.screen.multiSelect) {
      this.listConfig.selectionMode = SelectionMode.Multiple;
    } else {
      this.listConfig.selectionMode = SelectionMode.Single;
    }
    this.listConfig.numResultsPerPage = Number.MAX_VALUE;
    this.listConfig.items = this.screen.items;
    this.listConfig.defaultSelectItemIndex = 0;
    this.scrollToTop();
  }

  public onItemListChange(event: any[]): void {
  }

  public onItemChange(event: any): void {
    this.index = this.listConfig.items.indexOf(event);
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
