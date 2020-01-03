import { Component, OnInit, Injector, OnDestroy, HostListener } from '@angular/core';
import { Subscription } from 'rxjs';

import { IActionItem } from '../../core/actions/action-item.interface';
import { PosScreen } from '../../screens-with-parts/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { OnBecomingActive } from '../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../core/life-cycle-interfaces/leaving-active.interface';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';

@ScreenComponent({
    name: 'SelfCheckoutHome'
})
@Component({
    selector: 'app-self-checkout-home',
    templateUrl: './self-checkout-home.component.html',
    styleUrls: ['./self-checkout-home.component.scss']

})
export class SelfCheckoutHomeComponent extends PosScreen<any> implements
    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    public menuItems: IActionItem[];
    private actionSent = false;

    private scanServiceSubscription: Subscription;

    constructor(injector: Injector, private scannerService: ScannerService) {
        super(injector);
    }

    @HostListener('document:click', [])
    @HostListener('document:touchstart', [])
    begin() {
        this.doAction(this.screen.action);
    }

    buildScreen() {
        this.actionSent = false;
        this.menuItems = this.screen.menuItems;
    }

    onEnter(value: string) {
        this.doAction('Save');
    }

    getClass(): string {
        // return 'main-menu-grid-list';
        return 'foo';
    }

    onMenuItemClick(menuItem: IActionItem) {
        if (!this.actionSent) {
            this.doAction(menuItem);
            this.actionSent = true;
        }
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
        // this.scannerService.stopScanning();
        super.ngOnDestroy();
    }

    private registerScanner() {
        if (typeof this.scanServiceSubscription === 'undefined' || this.scanServiceSubscription === null) {
            this.scanServiceSubscription = this.scannerService.startScanning().subscribe(scanData => {
                this.doAction({ action: this.screen.action }, scanData);
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
