import { IDevicePlugin, IDeviceRequest, IDialog, ActionIntercepter, ActionIntercepterBehaviorType, IPlugin } from '../common';
import { SessionService, DeviceService } from '../services';
import { CordovaDevicePlugin } from '../common/cordova-device-plugin';
import { Scan } from '../common/scan';

export class BarcodeScannerPlugin extends CordovaDevicePlugin {

    pluginId = 'barcodeScannerPlugin';
    pluginName = this.pluginId;
    private pluginImpl: IPlugin;

    constructor() {
        super('barcodeScanner');
    }

    init(successCallback: () => void, errorCallback: (error?: string) => void): void {
        successCallback();
    }

    processRequest(deviceRequest: IDeviceRequest, successCallback: (response: any) => void, errorCallback: (error: string) => void) {
        console.log(`Attempting to invoke camera scanner via plugin '${this.pluginId}'`);
        this.impl.scan(
          function (result) {
            if (! result.cancelled) {
                successCallback(new Scan(result.text, result.format));
                console.log('We got a barcode\n' +
                'Result: ' + result.text + '\n' +
                'Format: ' + result.format + '\n' +
                'Cancelled: ' + result.cancelled);
              } else {
                successCallback(new Scan(null, null, true));
                console.log('Barcode scan cancelled');
            }
          },
          function (error) {
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

}
