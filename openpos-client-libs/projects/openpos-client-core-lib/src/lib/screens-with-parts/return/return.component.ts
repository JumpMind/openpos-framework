import { MatDialog, MatBottomSheet } from '@angular/material';
import { Component, ViewChild, AfterViewInit, OnInit, AfterViewChecked, ElementRef, Injector, OnDestroy } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { NavListComponent } from '../../shared/components/nav-list/nav-list.component';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ITotal } from '../../core/interfaces/total.interface';
import { TotalType } from '../../core/interfaces/total-type.enum';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { ITransactionReceipt } from '../../shared/components/receipt-card/transaction-receipt.interface';
import { OpenposMediaService, MediaBreakpoints } from '../../core/media/openpos-media.service';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { OnBecomingActive } from '../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../core/life-cycle-interfaces/leaving-active.interface';
import { MobileReturnReceiptsSheetComponent } from './mobile-return-receipts-sheet/mobile-return-receipts-sheet.component';

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
export class ReturnComponent extends PosScreen<any> implements OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    isMobile: Observable<boolean>;

    public size = -1;
    individualMenuClicked = false;

    public overFlowListSize: Observable<number>;

    public itemTotal: number;
    public receipts: ITransactionReceipt[];
    public removeReceiptAction: IActionItem;

    private scanServiceSubscription: Subscription;

    constructor(private scannerService: ScannerService, protected dialog: MatDialog, injector: Injector,
                media: OpenposMediaService, private bottomSheet: MatBottomSheet ) {
        super(injector);
        this.isMobile = media.observe(new Map([
            [MediaBreakpoints.MOBILE_PORTRAIT, true],
            [MediaBreakpoints.MOBILE_LANDSCAPE, false],
            [MediaBreakpoints.TABLET_PORTRAIT, true],
            [MediaBreakpoints.TABLET_LANDSCAPE, false],
            [MediaBreakpoints.DESKTOP_PORTRAIT, false],
            [MediaBreakpoints.DESKTOP_LANDSCAPE, false]
        ]));
    }

    buildScreen() {
        this.receipts = this.screen.receipts;
        this.removeReceiptAction = this.screen.removeReceiptAction;
        this.dialog.closeAll();
    }

    ngOnInit(): void {
        this.registerScanner();
    }

    onBecomingActive() {
        this.registerScanner();
    }

    onLeavingActive() {
        this.unregisterScanner();
    }

    ngOnDestroy(): void {
        this.unregisterScanner();
        this.scannerService.stopScanning();
        super.ngOnDestroy();
    }

    private registerScanner() {
        if (typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null) {
            this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
                this.doAction({ action: 'Scan' }, scanData);
            });
        }
    }

    private unregisterScanner() {
        if (this.scanServiceSubscription !== null) {
            this.scanServiceSubscription.unsubscribe();
            this.scanServiceSubscription = null;
        }
    }

    public onReceiptClick(event: any) {
        if (this.receipts) {
            const index = this.receipts.indexOf(event);
            this.doAction('TransactionDetails', index);
        }
    }

    openSheet(): void {
        console.log('Entering openSheet()');
        const ref = this.bottomSheet.open( MobileReturnReceiptsSheetComponent,
            {data: this.screen, panelClass: 'sheet'});
        this.subscriptions.add(new Subscription( () => ref.dismiss()));
        this.subscriptions.add(ref.afterDismissed().subscribe( item => {
            if (item !== undefined && item !== null) {
                if (typeof item === 'object') {
                    this.doAction(this.removeReceiptAction, item.transactionNumber);
                } else if (typeof item === 'number') {
                    this.doAction('TransactionDetails', item);
                }
            }
        }));
    }

}
