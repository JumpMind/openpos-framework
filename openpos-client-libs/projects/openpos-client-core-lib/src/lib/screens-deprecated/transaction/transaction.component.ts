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
import { ISelectableListData } from '../../shared/components/selectable-item-list/selectable-list-data.interface';

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
  listData: Observable<ISelectableListData<ISellItem>>;
  listConfig = new SelectableItemListComponentConfiguration();
  selectedItems: ISellItem[] = new Array<ISellItem>();
  selectedItemIndexes: number[] = new Array<number>();
  individualMenuClicked = false;

  transactionMenuPrompt: string;
  transactionMenu: IActionItemGroup;

  public overFlowListSize: Observable<number>;

  public items: ISellItem[];
  public amountTotals: ITotal[];
  public itemTotal: number;

  constructor(devices: DeviceService, private observableMedia: ObservableMedia, protected dialog: MatDialog) {
    super();
  }

  buildScreen() {
    this.selectedItems = this.screen.items
      .filter(item => this.screen.selectedItems.find(selectedItem => item.index === selectedItem.index));
    this.selectedItemIndexes = this.screen.selectedItems.map(item => item.index);

    const allItems = new Map<number, ISellItem>();
    const allDisabledItems = new Map<number, ISellItem>();
    for (let i = 0; i < this.screen.items.length; i++) {
        const item = this.screen.items[i];
        allItems.set(i, item);
        if (!item.enabled) {
            allDisabledItems.set(i, item);
        }
    }
    this.listData = new Observable<ISelectableListData<ISellItem>>((observer) => {
        observer.next({
            items: allItems,
            disabledItems: allDisabledItems,
        } as ISelectableListData<ISellItem>);
    });

    this.listConfig = new SelectableItemListComponentConfiguration();
    this.listConfig.selectionMode = SelectionMode.Multiple;
    this.listConfig.numItemsPerPage = Number.MAX_VALUE;
    this.listConfig.totalNumberOfItems = this.screen.items.length;

    this.items = this.screen.items;
    this.amountTotals = this.screen.totals ? (<ITotal[]>this.screen.totals).filter(t => t.type === TotalType.Amount) : null;
    const screenItemTotal = this.screen.totals ?
    (<ITotal[]>this.screen.totals).find(t => t.type === TotalType.Quantity && t.name === 'itemTotal') : null;
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
    let options = [];
    if (items.length > 1) {
      options = this.screen.multiSelectedMenuItems;
    } else {
      options = items[0].menuItems;
    }
    const dialogRef = this.dialog.open(NavListComponent, {
      width: '70%',
      data: {
        optionItems: options,
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

  public onItemListChange(event: number[]): void {
    this.selectedItemIndexes = event;
    this.session.onValueChange('SelectedItemsChanged', this.screen.items.filter(item => this.selectedItemIndexes.includes(item.index)));
  }

  public onMenuAction(event: any) {
    if (event.menuItem && event.payload) {
      this.onMenuItemClick(event.menuItem, event.payload);
    } else {
      this.onMenuItemClick(event);
    }
  }

}
