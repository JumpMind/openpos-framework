import { Component, OnDestroy, Injector } from '@angular/core';
import { PosScreen } from '../../screens-deprecated/pos-screen/pos-screen.component';
import { ScreenComponent } from '../../shared/decorators/screen-component.decorator';
import { PriceCheckerItemDetailInterface } from './price-checker-item-detail.interface';
import { ScannerService } from '../../core/platform-plugins/scanners/scanner.service';

@ScreenComponent({
    name: 'PriceCheckerItemDetail'
})
@Component({
    selector: 'app-price-checker-item-detail',
    templateUrl: 'price-checker-item-detail.component.html',
    styleUrls: ['./price-checker-item-detail.component.scss']
})
export class PriceCheckerItemDetailComponent extends PosScreen<PriceCheckerItemDetailInterface> implements OnDestroy {
    constructor( private scannerService: ScannerService, injector: Injector) {
        super(injector);
    }

    buildScreen() {
        this.scannerService.startScanning().subscribe( m => this.doAction(this.screen.scanAction, m));
    }

    ngOnDestroy(): void {
        this.scannerService.stopScanning();
    }

    onPrint() {
        this.doAction(this.screen.printButton);
    }

}
