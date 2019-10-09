import { Component, Injector, OnInit, OnDestroy, AfterViewChecked, ViewChild, ElementRef } from '@angular/core';
import { SaleInterface } from './sale.interface';
import { Observable } from 'rxjs/internal/Observable';
import { MatDialog } from '@angular/material';
import { PosScreen } from '../pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { OpenposMediaService } from '../../core/services/openpos-media.service';
import { ITotal } from '../../core/interfaces/total.interface';

import { Subscription } from 'rxjs';
import { OnBecomingActive } from '../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../core/life-cycle-interfaces/leaving-active.interface';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { IActionItem } from '../../core/actions/action-item.interface';

@ScreenComponent({
    name: 'Sale'
})
@Component({
    selector: 'app-sale',
    templateUrl: './sale.component.html',
    styleUrls: ['./sale.component.scss']
})
export class SaleComponent extends PosScreen<SaleInterface> implements
    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive, AfterViewChecked {

    overFlowListSize: Observable<number>;
    trainingDrawerOpen = false;
    totals: ITotal[];

    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;

    initialized = false;

    public items: ISellItem[];
    public size = -1;

    private scanServiceSubscription: Subscription;

    constructor(private scannerService: ScannerService, protected mediaService: OpenposMediaService,
        protected dialog: MatDialog, injector: Injector) {
        super(injector);
        this.overFlowListSize = this.mediaService.mediaObservableFromMap(new Map([
            ['xs', 3],
            ['sm', 3],
            ['md', 4],
            ['lg', 5],
            ['xl', 5]
        ]));

    }

    buildScreen() {
        // Reallocate totals array to force change detection in child app-overflow-list
        this.totals = this.screen.totals ? this.screen.totals.slice() : [];
        this.screen.customerName = this.screen.customerName != null && this.screen.customerName.length > 10 ?
            this.screen.customerName.substring(0, 10) + '...' : this.screen.customerName;
        this.dialog.closeAll();
        this.items = this.screen.items;

    }

    onEnter(value: string) {
        this.doAction('Next', value);
    }

    public onMenuItemClick(menuItem: IActionItem) {
        if (menuItem.enabled) {
            this.doAction(menuItem);
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

    ngOnInit(): void {
        this.scrollToBottom();
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
}
