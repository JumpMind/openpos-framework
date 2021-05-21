import {Component, Injector, OnInit} from '@angular/core';
import {ScreenComponent} from '../../shared/decorators/screen-component.decorator';
import {SimulatedPeripheralService} from '../../core/services/simulated-peripheral-service';
import {PosScreen} from '../pos-screen/pos-screen.component';
import {SimulatedPeripheralInterface} from './simulated-peripheral.interface';
import {DomSanitizer, SafeHtml, SafeResourceUrl} from '@angular/platform-browser';
import {PersonalizationService} from "../../core/personalization/personalization.service";
import {DiscoveryService} from "../../core/discovery/discovery.service";

@ScreenComponent({
    name: 'SimulatedPeripheral'
})
@Component({
    selector: 'app-simulated-peripheral-viewer',
    templateUrl: './simulated-peripheral-viewer.component.html',
    styleUrls: ['./simulated-peripheral-viewer.component.scss']
})
export class SimulatedPeripheralViewerComponent extends PosScreen<SimulatedPeripheralInterface> implements OnInit {

    receiptUrl: SafeResourceUrl;
    receiptsUrlHistory = new Array<SafeResourceUrl>();
    readonly receiptsUrlHistoryMaxLength = 128;
    currentReceiptUrlIndex = -1;

    constructor(injector: Injector, private simulatedPeripheralService : SimulatedPeripheralService,
                private domSanitizer : DomSanitizer, private personalizationService : PersonalizationService,
                private discoveryService : DiscoveryService) {
        super(injector);
    }

    ngOnInit() {
        this.subscriptions.add(this.simulatedPeripheralService.getReceiptData().subscribe(m => this.updateReceiptUrl(m)));
    }

    updateReceiptUrl(deviceToken : string) {
        if (deviceToken) {
            const url = `${this.discoveryService.getServerBaseURL()}/document/previewDocument/${deviceToken}`;
            this.receiptUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(url);
            this.addReceiptUrlToHistory(this.receiptUrl);
        }
    }

    addReceiptUrlToHistory(receiptUrl : SafeResourceUrl) {
        if (this.receiptsUrlHistory.length >= this.receiptsUrlHistoryMaxLength) {
            this.receiptsUrlHistory.shift();
        }
        this.receiptsUrlHistory.push(receiptUrl);
        this.currentReceiptUrlIndex = this.receiptsUrlHistory.length - 1;
    }

    hasNextReceipt() {
        return this.currentReceiptUrlIndex + 1 < this.receiptsUrlHistory.length;
    }

    getNextReceipt() {
        if (this.hasNextReceipt()) {
            this.currentReceiptUrlIndex++;
            this.receiptUrl = this.receiptsUrlHistory[this.currentReceiptUrlIndex];
        }
    }

    hasPreviousReceipt() {
        return this.currentReceiptUrlIndex > 0;
    }

    getPreviousReceipt() {
        if (this.hasPreviousReceipt()) {
            this.currentReceiptUrlIndex--;
            this.receiptUrl = this.receiptsUrlHistory[this.currentReceiptUrlIndex];
        }
    }

    buildScreen() {
    }
}