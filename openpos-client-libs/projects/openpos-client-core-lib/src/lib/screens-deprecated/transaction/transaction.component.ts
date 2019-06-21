import { MatDialog } from '@angular/material';
import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { NavListComponent } from '../../shared/components/nav-list/nav-list.component';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ITotal } from '../../core/interfaces/total.interface';
import { TotalType } from '../../core/interfaces/total-type.enum';
import { IActionItemGroup } from '../../core/interfaces/action-item-group.interface';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { DeviceService } from '../../core/services/device.service';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';

/**
 * @ignore
 */
@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss']
})
export class TransactionComponent extends PosScreen<any> implements AfterViewInit, OnInit {

  @ViewChild('box') vc;
  initialized = false;
  listConfig = new SelectableItemListComponentConfiguration<ISellItem>();
  selectedItems: ISellItem[] = new Array<ISellItem>();
  selectedItemIndexes: number[] = new Array<number>();
  individualMenuClicked = false;

  transactionMenuPrompt: string;
  transactionMenu: IActionItemGroup;

  public overFlowListSize: Observable<number>;

  public items: ISellItem[];
  public amountTotals: ITotal[];
  public itemTotal: number;

  constructor(devices: DeviceService,
    private observableMedia: ObservableMedia, protected dialog: MatDialog) {
    super();
  }

  buildScreen() {
    this.selectedItems = this.screen.items.filter(item => this.screen.selectedItems.find(selectedItem => item.index === selectedItem.index));
    this.selectedItemIndexes = this.screen.selectedItems.map(item => item.index);
    this.listConfig = new SelectableItemListComponentConfiguration<ISellItem>();
    this.listConfig.selectionMode = SelectionMode.Multiple;
    this.listConfig.numResultsPerPage = Number.MAX_VALUE;
    this.listConfig.items = this.screen.items;
    this.items = this.screen.items;
    this.amountTotals = this.screen.totals ? (<ITotal[]>this.screen.totals).filter(t => t.type === TotalType.Amount) : null;
    const screenItemTotal = this.screen.totals ? (<ITotal[]>this.screen.totals).find(t => t.type === TotalType.Quantity && t.name === 'itemTotal') : null;
    this.itemTotal = screenItemTotal ? Number(screenItemTotal.amount) : this.items.length;
    if (this.screen.template) {
      this.transactionMenuPrompt = this.screen.template.transactionMenuPrompt;
      this.transactionMenu = this.screen.template.transactionMenu;
    }
    this.dialog.closeAll();
  }

  ngOnInit(): void {
    const sizeMap = new Map([
      ['xs', 3],
      ['sm', 3],
      ['md', 4],
      ['lg', 5],
      ['xl', 5]
    ]);

    let startSize = 3;
    sizeMap.forEach((size, mqAlias) => {
      if (this.observableMedia.isActive(mqAlias)) {
        startSize = size;
      }
    });
    this.overFlowListSize = this.observableMedia.asObservable().pipe(map(
      change => {
        return sizeMap.get(change.mqAlias);
      }
    ), startWith(startSize));
  }

  ngAfterViewInit(): void {
    this.initialized = true;
  }

  onEnter(value: string) {
    this.session.onAction('Next', value);
  }

  openItemDialog(item: ISellItem) {
    this.individualMenuClicked = true;
    this.openItemsDialog([item]);
  }

  openItemsDialog(items: ISellItem[]) {
    let optionItems = [];
    if (items.length > 1) {
      optionItems = this.screen.multiSelectedMenuItems;
    } else {
      optionItems = items[0].menuItems;
    }
    const dialogRef = this.dialog.open(NavListComponent, {
      width: '70%',
      data: {
        optionItems: optionItems,
        payload: this.getIndexes(items),
        disableClose: false,
        autoFocus: false
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.log.info('The dialog was closed');
    });
  }

  public getIndexes(items: ISellItem[]): number[] {
    const indexes = [];
    items.forEach(item => indexes.push(item.index));
    return indexes;
  }

  public onItemListChange(items: ISellItem[]): void {
    this.selectedItemIndexes = items.map(item => item.index);
    this.session.onValueChange('SelectedItemsChanged', items);
  }

  public onMenuAction(event: any) {
    if (event.menuItem && event.payload) {
      this.onMenuItemClick(event.menuItem, event.payload);
    } else {
      this.onMenuItemClick(event);
    }
  }

}
