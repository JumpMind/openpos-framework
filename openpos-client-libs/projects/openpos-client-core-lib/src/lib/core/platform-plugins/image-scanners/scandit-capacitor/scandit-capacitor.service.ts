import { Injectable } from '@angular/core';
import { Plugins as CapacitorPlugins, Capacitor } from '@capacitor/core';

import { Observable, throwError } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScanData } from '../../scanners/scan.interface';

import { ImageScanner, ImageScannerDef, ScannerViewRef } from '../image-scanner';

@Injectable() 
@ImageScannerDef('scandit-cap')
export class ScanditCapacitorImageScanner implements ImageScanner {
    constructor() {
        if (Capacitor.isPluginAvailable('ScanditNative')) {
            CapacitorPlugins.ScanditNative.initialize({
                apiKey: 'ARN/oCgVCGzbEFujPh+FQ94sXhF6A9ok9kmmdG1v11V7S7ipXlnPqf9NBYJkEwim7XsZSgR0RtadNhGtFGyktsMDKtBJJh4Gahfn1+1BTOFlUsmiSCbIulsm2zRNM7yZhA6UidAJNveJpFPpngIwaGZbMPtwYcyW+tyKpD+WGbjWnHhPGl0iufLv/e1byT+wG5y6e9NkfthlCyJ8GyJ79NB6lxRLEkEOb0kSMMTABvPe464/TjBY+/K14p+kG0NEzAcYNWVX2HIzIccJxdEKXnqmWwWYulTTPteMDpJIKUZQK+iqkeifssj/HblPye8yYuoWzasFnL47Q1T5hOg0+LXSZ1HbSqV+/9WcRc2s6m/qzPQfnyAmH75gypHXsGfX/VVPQbLorkfqGmpbH0zP7RXa+UdnGU0DsOpH/WjFNA9k3w6xT9PavaQ4MQ4yA7MaTN438FbBultRUxuQJ7mYeTbGZZtNkUVqYtM/0pJB0xzS7MjGmxBnO/HElYtvGgN4V/J8lmfsoS3tZOCMzc9ePgCc4pwRDbaYWAEUID529JKp7YAkhdX93d8Vu+xjbspUBWcQx26gGms0m6FiV9humB+dnnGR4DIG4o0RvVpPk18tkhM2Ki+CeNHwNdf/2gkMVQLIlUDIbBeiaAXaG77w/odNQo/GKQvNzq3v56DZDBPUWifGBQD7sCuKQccBb+1XbLB0sQT2V2tg2PWLYb4WLzl2wapUfr8tzm8MBLI1jMGnTJVqoZVjI/f3yC5siOO1HnxE28/xlkqd3VE/IGINfelacaDKTlJgheAl0fiU9qol/LM='
            });
        }
    }

    beginScanning(view: ScannerViewRef): Observable<IScanData> {
        if (!Capacitor.isPluginAvailable('ScanditNative')) {
            return throwError('the scandit plugin is not available');
        }

        return new Observable(() => {
            CapacitorPlugins.ScanditNative.addView();

            const updateViewSub = view.viewChanges().pipe(
                mergeMap(d => CapacitorPlugins.ScanditNative.updateView(d))
            ).subscribe();

            return () => {
                updateViewSub.unsubscribe();
                CapacitorPlugins.ScanditNative.removeView();
            };
        });
    }
}
