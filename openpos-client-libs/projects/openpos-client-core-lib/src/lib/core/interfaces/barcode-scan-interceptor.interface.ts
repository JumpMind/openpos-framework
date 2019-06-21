import { Scan } from '../oldplugins/scan';

/** Implement this interface and set your interceptor on the BarcodeScannerPlugin
 *  in order to receive a callback on each barcode scan.
 */
export interface IBarcodeScanInterceptor {
    /**
     * Invoked by the BarCodeScannerPlugin to allow for clients to modify
     * the scan info emitted/returned by the BarCodeScannerPlugin.
     * @param scan The incoming scan data from the scanner.
     * @returns The potentially modified scan data.
     */
    interceptScan(scan: Scan): Scan;
}
