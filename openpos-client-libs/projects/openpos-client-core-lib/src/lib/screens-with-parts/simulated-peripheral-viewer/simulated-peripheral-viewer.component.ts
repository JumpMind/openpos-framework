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
    secondReceiptUrl: SafeResourceUrl;

    constructor(injector: Injector, private simulatedPeripheralService : SimulatedPeripheralService,
                private domSanitizer : DomSanitizer, private personalizationService : PersonalizationService,
                private discoveryService : DiscoveryService) {
        super(injector);
    }

    ngOnInit() {
        this.subscriptions.add(this.simulatedPeripheralService.getReceiptData().subscribe(m => this.updateReceiptUrl(m)));
    }

    updateReceiptUrl(deviceToken : string)
    updateReceiptUrl(deviceToken : Array<string>)
    updateReceiptUrl(deviceToken : any) {
        if (deviceToken) {
            if (Array.isArray(deviceToken)) {
                if (deviceToken.length == 2 && deviceToken[0] && deviceToken[1]) {
                    const firstUrl = `${this.discoveryService.getServerBaseURL()}/document/previewDocument/${deviceToken[0]}`;
                    this.receiptUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(firstUrl);

                    const secondUrl = `${this.discoveryService.getServerBaseURL()}/document/previewDocument/${deviceToken[1]}`;
                    this.secondReceiptUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(secondUrl);
                }
            } else {
                const url = `${this.discoveryService.getServerBaseURL()}/document/previewDocument/${deviceToken}`;
                this.receiptUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(url);
                this.secondReceiptUrl = null;
            }
        }
    }

    buildScreen() {
    }
}