import { Component, ViewChild, ElementRef, Input, Output, EventEmitter, AfterViewChecked } from '@angular/core';
import { ISellItem } from '../../../core/interfaces/sell-item.interface';
import { SelectableItemListComponentConfiguration } from '../selectable-item-list/selectable-item-list.component';
import { Configuration } from '../../../configuration/configuration';
import { IActionItem } from '../../../core/interfaces/action-item.interface';
import { IActionItemGroup } from '../../../core/interfaces/action-item-group.interface';
import { IItem } from '../../../core/interfaces/item.interface';

@Component({
  selector: 'app-transaction-item-list',
  templateUrl: './transaction-item-list.component.html',
  styleUrls: ['./transaction-item-list.component.scss']
})
export class TransactionItemListComponent implements AfterViewChecked {

  @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;

  @Input() listConfig: SelectableItemListComponentConfiguration<ISellItem>;
  @Input() selectedItems: ISellItem[];
  @Input() selectedItemIndexes: number[];
  @Input() multiSelectedMenuItems: IActionItem[];
  @Input() transactionMenu: IActionItemGroup;
  @Input() prompt: string;
  @Input() readOnly: boolean;

  @Output() selectedItemListChange = new EventEmitter<ISellItem[]>();
  @Output() menuAction = new EventEmitter<any>();

  individualMenuClicked = false;

  size = -1;

  public onItemListChange(items: ISellItem[]): void {
    this.selectedItems = items;
    if (this.individualMenuClicked) {
      this.individualMenuClicked = false;
      return;
    }
    this.selectedItemListChange.emit(items);
  }

  public onItemActionsMultiMenulick(menuItem: IActionItem, selectedItemIndexes: number[],  selectedItems: IItem[]) {
    if (menuItem.enabled  && selectedItems && selectedItems.length > 0) {
      this.menuAction.emit({ menuItem, payload: selectedItems.map(i => i.index) });
    } else {
      this.onMenuItemClick(menuItem, selectedItemIndexes);
    }
  }

  public onMenuItemClick(menuItem: IActionItem, payload?: number[]) {
    if (menuItem.enabled && payload) {
      this.menuAction.emit({ menuItem, payload });
    } else if (menuItem.enabled) {
      this.menuAction.emit(menuItem);
    }
  }

  ngAfterViewChecked() {
    if (this.listConfig && this.listConfig.items && this.size !== this.listConfig.items.length) {
      this.scrollToBottom();
      this.size = this.listConfig.items.length;
    }
  }

  scrollToBottom(): void {
    try {
      this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
    } catch (err) { }
  }

  public keybindsEnabled() {
    return Configuration.enableKeybinds;
  }

}
