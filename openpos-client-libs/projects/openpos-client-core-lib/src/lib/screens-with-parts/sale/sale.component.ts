import { AfterViewInit, ChangeDetectorRef, Component, Injector, ViewChild } from '@angular/core';
import { SaleInterface } from './sale.interface';
import { MatBottomSheet, MatDialog } from '@angular/material';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ITotal } from '../../core/interfaces/total.interface';

import { merge, Observable, Subject, Subscription } from 'rxjs';
import { IActionItem } from '../../core/actions/action-item.interface';
import { MediaBreakpoints, OpenposMediaService } from '../../core/media/openpos-media.service';
import { Configuration } from './../../configuration/configuration';
import { MobileSaleOrdersSheetComponent } from './mobile-sale-orders-sheet/mobile-sale-orders-sheet.component';
import { KeyPressProvider } from '../../shared/providers/keypress.provider';
import { delay, filter, takeUntil, tap } from 'rxjs/operators';
import { SaleItemCardListComponent } from '../../shared/screen-parts/sale-item-card-list/sale-item-card-list.component';
import { ISellItem } from '../../core/interfaces/sell-item.interface';


@ScreenComponent({
    name: 'Sale'
})
@Component({
    selector: 'app-sale',
    templateUrl: './sale.component.html',
    styleUrls: ['./sale.component.scss']
})
export class SaleComponent extends PosScreen<SaleInterface> implements AfterViewInit {
    @ViewChild('scrollList') private saleItemCardList: SaleItemCardListComponent;

    isMobile$: Observable<boolean>;
    totals: ITotal[];
    Configuration = Configuration;
    initialized = false;
    removeOrderAction: IActionItem;
    buildScreen$ = new Subject();
    stop$: Observable<any>;
    sidenavOpened = false;
    isEmpty = true;

    constructor(protected dialog: MatDialog,
                injector: Injector,
                media: OpenposMediaService,
                private bottomSheet: MatBottomSheet,
                private keyPressProvider: KeyPressProvider,
                private changeDetectorRef: ChangeDetectorRef) {
        super(injector);
        this.isMobile$ = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, true],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
        this.stop$ = merge(this.destroyed$, this.buildScreen$);
    }

    buildScreen() {
        this.buildScreen$.next();
        // Reallocate totals array to force change detection in child app-overflow-list
        this.totals = this.screen.totals ? this.screen.totals.slice() : [];
        this.screen.customerName = this.screen.customerName != null && this.screen.customerName.length > 10 ?
            this.screen.customerName.substring(0, 10) + '...' : this.screen.customerName;
        this.removeOrderAction = this.screen.removeOrderAction;
        this.dialog.closeAll();

        if (this.screen.logoutButton) {
            this.keyPressProvider.globalSubscribe(this.screen.logoutButton).pipe(
                takeUntil(this.stop$)
            ).subscribe(action => this.doAction(action));
        }
    }

    ngAfterViewInit() {
        if (this.saleItemCardList && this.saleItemCardList.items$) {
            this.saleItemCardList.items$.subscribe(items => this.isEmpty === !items || items.length === 0);
        }
    }

    onEnter(value: string) {
        this.doAction('Next', value);
    }

    public onMenuItemClick(menuItem: IActionItem) {
        if (menuItem.enabled) {
            this.doAction(menuItem);
        }
    }

    public onOrderClick(event: any) {
        if (this.screen.orders) {
            const index = this.screen.orders.indexOf(event);
            this.doAction('OrderDetails', index);
        }
    }

    openSheet(): void {
        console.log('Entering openSheet()');
        const ref = this.bottomSheet.open(MobileSaleOrdersSheetComponent,
            {data: this.screen, panelClass: 'sheet'});
        this.subscriptions.add(new Subscription(() => ref.dismiss()));
        this.subscriptions.add(ref.afterDismissed().subscribe(item => {
            if (item !== undefined && item !== null) {
                if (typeof item === 'object') {
                    this.doAction(this.removeOrderAction, item.number);
                } else if (typeof item === 'number') {
                    this.doAction('OrderDetails', item);
                }
            }
        }));
    }

    onItemsChanged(items: ISellItem[]): void {
        this.isEmpty = items.length === 0;
        // Angular gets angry if you update the current view when a child property changes. This prevents the error:
        // ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked.
        this.changeDetectorRef.detectChanges();
    }
}
