import { Subscription } from 'rxjs';
import { Directive, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { BarcodeScannerPlugin } from '../../core/oldplugins/barcode-scanner.plugin';
import { CordovaService } from '../../core/services/cordova.service';
import { DialogService } from '../../core/services/dialog.service';
import { OldPluginService } from '../../core/services/old-plugin.service';
import { Scan } from '../../core/oldplugins/scan';
import { ActionMessage } from '../../core/messages/action-message';
import { MessageProvider } from '../providers/message.provider';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[barcodeScanPublisher]'
})
export class BarcodeScanPublisherDirective implements OnInit, OnDestroy {

    private barcodePlugin: BarcodeScannerPlugin;
    private barcodeEventSubscription: Subscription;
    private subscription: Subscription;
    constructor(el: ElementRef,
                private cordovaService: CordovaService,
                private messageProvider: MessageProvider,
                private dialogService: DialogService,
                private pluginService: OldPluginService) {
    }


    ngOnInit(): void {
        this.subscription =this.cordovaService.onDeviceReady.subscribe(message => {
            if (message) {
                console.info(`BarcodeScanPublisherDirective got deviceready`);
                this.pluginService.getDevicePlugin('barcodeScannerPlugin').then(plugin => {
                    console.info('BarcodeScanPublisherDirective INITTED, got barcodeScannerPlugin');
                    this.barcodePlugin = <BarcodeScannerPlugin> plugin;
                    // the onBarcodeScanned will only emit an event when client code passes a scan
                    // event to the plugin.  This won't be called for cordova barcodescanner plugin
                    // camera-based scan events.  It should only be used for third party scan events
                    // which come from other sources such as a scan device
                    this.barcodeEventSubscription = this.barcodePlugin.onBarcodeScanned.subscribe({
                        next: (scan: Scan) => {
                            this.publishBarcode(scan);
                        }
                    });
                }).catch( error => console.info(`Failed to get barcodeScannerPlugin.  Error: ${error}`) );
            }
        });
    }

    ngOnDestroy(): void {
        if (this.barcodeEventSubscription) {
            this.barcodeEventSubscription.unsubscribe();
        }
        if( this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    publishBarcode(scan: Scan) {
        if (! this.dialogService.isDialogOpen()) {
            // Publish barcode to the server
            console.info(`Got barcode scan, publishing '${scan.value}'...`);
            this.messageProvider.sendMessage(new ActionMessage('Scan', true, scan));
        } else {
            console.warn(`Not publishing barcode '${scan.value}' because a dialog is showing`);
        }
    }
}
