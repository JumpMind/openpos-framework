import { Component, ViewChild, AfterViewChecked, ElementRef, OnInit, Injector, OnDestroy } from '@angular/core';
import { ISellItem } from '../../core/interfaces/sell-item.interface';
import { IActionItem } from '../../core/actions/action-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { DeviceService } from '../../core/services/device.service';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { Subscription } from 'rxjs';
import { OnBecomingActive } from '../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../core/life-cycle-interfaces/leaving-active.interface';
import { ImageService } from '../../core/services/image.service';

@ScreenComponent({
    name: 'SelfCheckoutSale'
})
@Component({
    selector: 'app-self-checkout-sale',
    templateUrl: './self-checkout-sale.component.html',
    styleUrls: ['./self-checkout-sale.component.scss']
})
export class SelfCheckoutSaleComponent extends PosScreen<any> implements
    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive, AfterViewChecked {
    @ViewChild('scrollList', { read: ElementRef }) private scrollList: ElementRef;

    initialized = false;

    public items: ISellItem[];
    public size = -1;


    private scanServiceSubscription: Subscription;

    constructor(public devices: DeviceService, injector: Injector, private scannerService: ScannerService) {
        super(injector);
    }

    buildScreen() {
        this.items = this.screen.items;
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
