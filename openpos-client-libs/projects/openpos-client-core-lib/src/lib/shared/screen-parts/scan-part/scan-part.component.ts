import { ScreenPart } from '../../decorators/screen-part.decorator';
import { Component, OnInit, OnDestroy, Injector } from '@angular/core';
import { ScreenPartComponent } from '../screen-part';
import { OnBecomingActive } from '../../../core/life-cycle-interfaces/becoming-active.interface';
import { OnLeavingActive } from '../../../core/life-cycle-interfaces/leaving-active.interface';
import { Subscription } from 'rxjs';
import { ScannerService } from '../../../core/platform-plugins/scanners/scanner.service';
import { ScanInterface } from './scan-part.interface';

@ScreenPart({
    name: 'scan'
})
@Component({
    selector: 'app-scan-part',
    templateUrl: './scan-part.component.html',
    styleUrls: ['./scan-part.component.scss']
})
export class ScanPartComponent extends ScreenPartComponent<ScanInterface> implements
    OnInit, OnDestroy, OnBecomingActive, OnLeavingActive {

    private scanServiceSubscription: Subscription;

    constructor(injector: Injector, private scannerService: ScannerService) {
        super(injector);
    }

    ngOnInit(): void {
        super.ngOnInit();
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
                if (this.screenData.scanActionName) {
                    this.doAction({ action: this.screenData.scanActionName }, scanData);
                }
            });
        }
    }

    private unregisterScanner() {
        if (this.scanServiceSubscription !== null) {
            this.scanServiceSubscription.unsubscribe();
            this.scanServiceSubscription = null;
        }
    }

    screenDataUpdated() {
    }

}
