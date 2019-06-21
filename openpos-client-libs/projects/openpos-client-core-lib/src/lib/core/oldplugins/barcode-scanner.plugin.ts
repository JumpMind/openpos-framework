import { EventEmitter } from '@angular/core';
import { IOldPlugin } from './oldplugin.interface';
import { IDeviceRequest } from './device-request.interface';
import { CordovaDevicePlugin } from './cordova-device-plugin';
import { Scan } from './scan';
import { IBarcodeScanInterceptor } from '../interfaces/barcode-scan-interceptor.interface';

/**
 * Wrapper around the Cordova barcode scanner plugin to allow for intercepting the barcodes
 * returned from the barcode scanner plugin and configuring the plugin itself.
 */
export class BarcodeScannerPlugin extends CordovaDevicePlugin {

    pluginId = 'barcodeScannerPlugin';
    pluginName = this.pluginId;
    onBarcodeScanned: EventEmitter<Scan> = new EventEmitter<Scan>();
    private pluginImpl: IOldPlugin;
    private _barcodeScanInterceptor: IBarcodeScanInterceptor;

    constructor() {
        super('barcodeScanner');
    }

    init(successCallback: () => void, errorCallback: (error?: string) => void): void {
        successCallback();
    }

    set barcodeScanInterceptor(interceptor: IBarcodeScanInterceptor) {
        this._barcodeScanInterceptor = interceptor;
    }

    get barcodeScanInterceptor(): IBarcodeScanInterceptor {
        return this._barcodeScanInterceptor;
    }

    processRequest(deviceRequest: IDeviceRequest, successCallback: (response: any) => void, errorCallback: (error: string) => void) {
        this.log.info(`Attempting to invoke camera scanner via plugin '${this.pluginId}'`);
        this.impl.scan( (result) => {
            if (! result.cancelled) {
                let scan = new Scan(result.text, result.format);
                scan = this.optionallyInterceptScan(scan);
                successCallback(scan);
                this.log.info('We got a barcode\n' +
                'Result: ' + scan.value + '\n' +
                'Format: ' + scan.format + '\n' +
                'Cancelled: ' + scan.cancelled);
              } else {
                successCallback(new Scan(null, null, true));
                this.log.info('Barcode scan cancelled');
            }
          },
          (error) => {
            const msg = `Scanning failed: ${error}`;
            console.error(msg);
            errorCallback(msg);
          },
          {
            preferFrontCamera: false, // iOS and Android
            showFlipCameraButton: false, // iOS and Android
            showTorchButton: false, // iOS and Android
            torchOn: false, // Android, launch with the torch switched on (if available)
            saveHistory: false, // Android, save scan history (default false)
            prompt: 'Place a barcode inside the scan area', // Android
            resultDisplayDuration: 500, // Android, display scanned text for X ms. 0 suppresses it entirely, default 1500
            formats: 'CODE_128,CODE_39,EAN_8,EAN_13,UPC_A,UPC_E,QR_CODE,DATA_MATRIX', // default: all but PDF_417 and RSS_EXPANDED
            orientation: 'landscape', // Android only (portrait|landscape), default unset so it rotates with the device
            disableAnimations: false, // iOS
            disableSuccessBeep: false // iOS and Android
          }
        );
    }

    protected optionallyInterceptScan(scan: Scan): Scan {
        let lScan = scan;
        if (this.barcodeScanInterceptor) {
            this.log.info(`Scan intercepted by ` +
                `'${this.barcodeScanInterceptor.constructor ? this.barcodeScanInterceptor.constructor.name : 'IBarcodeScanInterceptor object'}'`);
            lScan = this.barcodeScanInterceptor.interceptScan(scan);
        }

        return lScan;
    }

    emitBarcode(scan: Scan) {
        const s = this.optionallyInterceptScan(scan);
        this.log.info(`Emitting barcode from BarcodeScannerPlugin: ${JSON.stringify(s)}`);
        this.onBarcodeScanned.emit(s);
    }
}
