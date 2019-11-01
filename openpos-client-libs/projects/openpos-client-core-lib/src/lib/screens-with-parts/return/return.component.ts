import { MatDialog } from '@angular/material';
import { Component, ViewChild, AfterViewInit, OnInit, AfterViewChecked, ElementRef, Injector } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { NavListComponent } from '../../shared/components/nav-list/nav-list.component';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ITotal } from '../../core/interfaces/total.interface';
import { TotalType } from '../../core/interfaces/total-type.enum';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
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
    individualMenuClicked = false;

    public overFlowListSize: Observable<number>;

    public items: ISellItem[];
    public itemTotal: number;
    public receipts: ITransactionReceipt[];
    public removeReceiptAction: IActionItem;

    constructor(
        private observableMedia: ObservableMedia, protected dialog: MatDialog, injector: Injector) {
        super(injector);
    }

    buildScreen() {
        this.items = this.screen.items;
        this.receipts = this.screen.receipts;
        this.removeReceiptAction = this.screen.removeReceiptAction;
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

    public onReceiptClick(event: any) {
        if (this.receipts) {
            const index = this.receipts.indexOf(event);
            this.doAction('TransactionDetails', index);
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
