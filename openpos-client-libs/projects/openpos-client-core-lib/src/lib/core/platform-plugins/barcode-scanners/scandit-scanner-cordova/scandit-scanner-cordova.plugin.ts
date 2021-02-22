import { Injectable } from '@angular/core';
import { ConnectableObservable, interval, Observable } from 'rxjs';
import { debounce, filter, map, mergeMap, publishReplay, take } from 'rxjs/operators';
import { SessionService } from '../../../services/session.service';
import { IPlatformPlugin } from '../../platform-plugin.interface';
import { ScanditBarcodeUtils } from './scandit-to-openpos-barcode-type';

import { ImageScanner, ScanData, ScannerViewRef } from '../scanner';

declare var Scandit: any;
@Injectable()
export class ScanditScannerCordovaPlugin implements ImageScanner, IPlatformPlugin {
    private readonly scandit$: Observable<{context, capture, camera, viewfinder}>;

    constructor(sessionService: SessionService) {
        const scanditSession = sessionService.getMessages('ConfigChanged').pipe(
            filter(m => m.configType === 'ScanditCordova'),
            map(config => {
                const settings = new Scandit.BarcodeCaptureSettings();
                const licenseKey: string = config.licenseKey;

                Object.getOwnPropertyNames(config).forEach(propertyName => {
                    if (settings.hasOwnProperty(propertyName)) {
                        settings[propertyName] = config[propertyName];
                    }
                });

                if (config.enabledCodes) {
                    config.enabledCodes.split(',').forEach(code => {
                        settings.enableSymbology(ScanditBarcodeUtils.convertFromOpenposType(code.trim()), true);
                    });
                }

                let viewfinder = ScanditScannerCordovaPlugin.getViewfinderType('Laser');
                if (config.viewFinderType) {
                    viewfinder = ScanditScannerCordovaPlugin.getViewfinderType(config.viewFinderType);
                }

                let enableVibrate = true;
                if (config.enableVibrate) {
                    enableVibrate = config.enableVibrate;
                }

                let enableBeep = true;
                if (config.enableBeep) {
                    enableBeep = config.enableBeep;
                }

                const context = Scandit.DataCaptureContext.forLicenseKey(licenseKey);

                const capture = Scandit.BarcodeCapture.forContext(context, settings);
                const feedback = Scandit.BarcodeCaptureFeedback.default;

                feedback.success = new Scandit.Feedback(
                    enableVibrate ? Scandit.Vibration.defaultVibration : null,
                    enableBeep ? Scandit.Sound.defaultSound : null
                );

                capture.feedback = feedback;
                capture.isEnabled = false;

                let camera = Scandit.Camera.default;

                if (config.camera === 'front') {
                    camera = Scandit.Camera.atPosition(Scandit.CameraPosition.UserFacing);
                } else if (config.camera === 'rear') {
                    camera = Scandit.Camera.atPosition(Scandit.CameraPosition.WorldFacing);
                }

                camera.applySettings(Scandit.BarcodeCapture.recommendedCameraSettings);

                context.setFrameSource(camera);

                return {
                    context,
                    capture,
                    camera,
                    viewfinder
                };
            }),
            publishReplay(1)
        ) as ConnectableObservable<{context, capture, camera, viewfinder}>;

        // make it hot so we can start capturing changes.
        scanditSession.connect();

        this.scandit$ = scanditSession;
    }

    beginScanning(view: ScannerViewRef): Observable<ScanData> {
        return this.scandit$.pipe(
            mergeMap(scandit => new Observable<ScanData>(observer => {
                scandit.camera.switchToDesiredState(Scandit.FrameSourceState.On);

                const listener = {
                    didScan: (barcodeCapture, session) => {
                        barcodeCapture.isEnabled = false;

                        const scanData = <ScanData> {
                            type: ScanditBarcodeUtils.convertToOpenposType(session.newlyRecognizedBarcodes[0].symbology, session.newlyRecognizedBarcodes[0].data),
                            data: session.newlyRecognizedBarcodes[0].data
                        };

                        // Scandit adds a leading 0 to UPCA scans
                        if (scanData.type == 'UPCA') {
                            scanData.data = scanData.data.substring(1);
                        }

                        observer.next(scanData);
                    }
                };

                scandit.capture.addListener(listener);
                scandit.capture.isEnabled = true;

                const scanditView = Scandit.DataCaptureView.forContext(scandit.context);
                scanditView.connectToElement(view.element);

                const overlay = Scandit.BarcodeCaptureOverlay.withBarcodeCaptureForView(scandit.capture, view);
                overlay.viewfinder = scandit.viewfinder;

                return () => {
                    scanditView.removeOverlay(overlay);
                    scandit.capture.removeListener(listener);
                    scandit.camera.switchToDesiredState(Scandit.FrameSourceState.Off);
                    scandit.capture.isEnabled = false;
                };
            }).pipe(
                debounce(() => interval(1000))
            ))
        );
    }

    initialize(): Observable<string> {
        return this.scandit$.pipe(
            map(() => 'Waiting for Scandit configuration...'),
            take(1),
            map(() => 'Scandit for Cordova configured')
        );
    }

    name(): string {
        return 'ScanditCordova';
    }

    pluginPresent(): boolean {
        return window['Scandit'];
    }

    private static getViewfinderType(type?: 'Laser' | 'Rectangular' | 'Spotlight'): any | null {
        if (type) {
            switch (type) {
                case 'Laser':
                    console.log('Scandit Setting Laserline ViewFinder');
                    return new Scandit.LaserlineViewfinder();
                case 'Rectangular':
                    console.log('Scandit Setting Rectangular ViewFinder');
                    return new Scandit.RectangularViewfinder();
                case 'Spotlight':
                    console.log('Scandit Setting Spotlight ViewFinder');
                    return new Scandit.SpotlightViewfinder();
            }
        }

        return null;
    }
}
