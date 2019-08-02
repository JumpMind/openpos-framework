import { MatDialog } from '@angular/material';
import { Component, ViewChild, AfterViewInit, OnInit, AfterViewChecked, ElementRef } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SelectableItemListComponentConfiguration } from '../../shared/components/selectable-item-list/selectable-item-list.component';
import { NavListComponent } from '../../shared/components/nav-list/nav-list.component';
import { PosScreen } from '../pos-screen.component';
import { ITotal } from '../../core/interfaces/total.interface';
import { TotalType } from '../../core/interfaces/total-type.enum';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { SelectionMode } from '../../core/interfaces/selection-mode.enum';
import { IActionItem } from '../../core/interfaces/action-item.interface';
import { ISelectableListData } from '../../shared/components/selectable-item-list/selectable-list-data.interface';
import { ITransactionReceipt } from '../../shared/components/receipt-card/transaction-receipt.interface';

/**
 * @ignore
 */
@ScreenComponent({
    name: 'Return'
})
@Component({
    selector: 'app-return',
    templateUrl: './return.component.html',
    styleUrls: ['./return.component.scss']
})
export class ReturnComponent extends PosScreen<any> implements AfterViewInit, AfterViewChecked, OnInit {

    @ViewChild('scrollList') private scrollList: ElementRef;
    public size = -1;
    initialized = false;    // listData: Observable<ISelectableListData<ISellItem>>;
    selectedItems: ISellItem[] = new Array<ISellItem>();
    individualMenuClicked = false;

    public overFlowListSize: Observable<number>;

    public items: ISellItem[];
    public amountTotals: ITotal[];
    public itemTotal: number;
    public receipts: ITransactionReceipt[];
    public removeReceiptAction: IActionItem;

    constructor(
        private observableMedia: ObservableMedia, protected dialog: MatDialog) {
        super();
    }

    buildScreen() {
        this.selectedItems =
          this.screen.items.filter(item => this.screen.selectedItems.find(selectedItem => item.index === selectedItem.index));

        this.items = this.screen.items;
        this.amountTotals = this.screen.totals ? (this.screen.totals as ITotal[]).filter(t => t.type === TotalType.Amount) : null;
        const screenItemTotal =
          this.screen.totals ? (this.screen.totals as ITotal[]).find(t => t.type === TotalType.Quantity && t.name === 'itemTotal') : null;
        this.itemTotal = screenItemTotal ? Number(screenItemTotal.amount) : this.items.length;
        this.receipts = this.screen.receipts;
        this.removeReceiptAction = this.screen.removeReceiptAction;
        this.screen.customerName = this.screen.customerName != null && this.screen.customerName.length > 10 ?
            this.screen.customerName.substring(0, 10) + '...' : this.screen.customerName;
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

    returnButtonDisabled(): boolean {
        return !this.items || this.items.length === 0;
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
                optionItems,
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

    public onReceiptClick(event: any) {
        if (this.receipts) {
            const index = this.receipts.indexOf(event);
            this.session.onAction('TransactionDetails', index);
        }
    }

    public onItemListChange(event: number[]): void {
        const items = this.screen.items.filter(item => event.includes(item.index));
        this.session.onValueChange('SelectedItemsChanged', items);
    }

    public onMenuAction(event: any) {
        if (event.menuItem && event.payload) {
            this.onMenuItemClick(event.menuItem, event.payload);
        } else {
            this.onMenuItemClick(event);
        }
    }

    ngAfterViewChecked() {
        if (this.items && this.size !== this.items.length) {
            this.scrollToBottom();
            this.size = this.items.length;
        }
    }

    scrollToBottom(): void {
        try {
            this.scrollList.nativeElement.scrollTop = this.scrollList.nativeElement.scrollHeight;
        } catch (err) { }
    }

}
