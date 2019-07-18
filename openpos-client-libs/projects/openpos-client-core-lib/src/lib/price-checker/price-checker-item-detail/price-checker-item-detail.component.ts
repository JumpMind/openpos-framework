import { Component, OnDestroy, OnInit } from '@angular/core';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PriceCheckerItemDetailInterface } from './price-checker-item-detail.interface';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';
import { Subscription } from 'rxjs';

@ScreenComponent({
    name: 'PriceCheckerItemDetail'
})
@Component({
    selector: 'app-price-checker-item-detail',
    templateUrl: 'price-checker-item-detail.component.html',
    styleUrls: ['./price-checker-item-detail.component.scss']
})
export class PriceCheckerItemDetailComponent extends PosScreen<PriceCheckerItemDetailInterface> implements OnDestroy, OnInit {

    scannerSubscription: Subscription;

    constructor( private scannerService: ScannerService) {
        super();
    }

    ngOnInit(): void {
        this.scannerSubscription = this.scannerService.startScanning().subscribe( m => this.session.onAction(this.screen.scanAction, m));
    }

    buildScreen() {
    }

    ngOnDestroy(): void {
        this.scannerSubscription.unsubscribe();
        this.scannerService.stopScanning();
    }

    onPrint() {
        this.session.onAction(this.screen.printButton);
    }

}
