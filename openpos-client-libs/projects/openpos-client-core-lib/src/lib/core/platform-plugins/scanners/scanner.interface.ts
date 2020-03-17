import { Observable } from 'rxjs';
import { IScanData } from './scan.interface';

export interface IScanner {
    /**
     * Tell the scanner to start scanning
     * returns and observable with the scan data
     */
    startScanning(): Observable<IScanData>;

    /**
     * Tell the scanner to stop scanning
     */
    stopScanning();

    /**
     * Tells the scanner to trigger the aiming mechanism
     * For camera scanners this would open a live feed of the camera
     * For most hardware scanners this would o nothing.
     */
    triggerScan();
}
